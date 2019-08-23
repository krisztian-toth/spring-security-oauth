package hu.krisz.oauth.token;

import hu.krisz.oauth.dao.entity.token.RefreshToken;
import hu.krisz.oauth.dao.repository.RefreshTokenRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token store which persists the refresh token only using spring data JPA.
 *
 * @author krisztian.toth on 8-8-2019
 */
public class JpaRefreshTokenJwtTokenStore extends JwtTokenStore {

    private static final Log LOG = LogFactory.getLog(JpaRefreshTokenJwtTokenStore.class);

    private static final String CLIENT_ID = "client_id";
    private static final String GRANT_TYPE = "grant_type";

    private final RefreshTokenRepository refreshTokenRepository;
    private final ClientDetailsService clientDetailsService;
    private final UserDetailsService userDetailsService;

    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     *
     * @param jwtAccessTokenConverter a {@link JwtAccessTokenConverter}
     * @param refreshTokenRepository  the {@link RefreshTokenRepository}
     * @param clientDetailsService    the {@link ClientDetailsService}
     * @param userDetailsService      the {@link UserDetailsService}
     */
    public JpaRefreshTokenJwtTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter,
                                        RefreshTokenRepository refreshTokenRepository,
                                        ClientDetailsService clientDetailsService,
                                        UserDetailsService userDetailsService) {
        super(jwtAccessTokenConverter);
        this.refreshTokenRepository = refreshTokenRepository;
        this.clientDetailsService = clientDetailsService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Stores the refresh token in the database.
     *
     * @param refreshToken   The refresh token to store.
     * @param authentication The authentication associated with the refresh token.
     */
    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        // we only want to store refresh tokens with an expiry date
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            Authentication userAuthentication = authentication.getUserAuthentication();
            if (userAuthentication != null) {
                Object principal = userAuthentication.getPrincipal();
                String username;
                if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername();
                } else if (principal instanceof String) {
                    username = (String) principal;
                } else {
                    LOG.warn("Unknown type for principal: " + principal.getClass().toString()
                            + ", skipping persisting...");
                    return;
                }

                // If there's already a refresh token existing for the user, we delete it
                refreshTokenRepository.findByUsername(username).ifPresent(e -> {
                    refreshTokenRepository.delete(e);
                    refreshTokenRepository.flush();
                });

                Date expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
                String clientId = authentication.getOAuth2Request().getClientId();
                String grantType = authentication.getOAuth2Request().getGrantType();
                refreshTokenRepository.save(new RefreshToken(refreshToken.getValue(), username, clientId,
                        grantType, expiration.toInstant(), Instant.now()));
            }
        } else {
            LOG.warn("The provided refresh token is not of type ExpiringOAuth2RefreshToken, skipping persisting...");
        }
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return refreshTokenRepository.findByToken(tokenValue)
                .map(token -> new DefaultExpiringOAuth2RefreshToken(token.getToken(), Date.from(token.getExpiresAt())))
                .orElse(null);

        // TODO approvalStore
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        refreshTokenRepository.findByToken(token.getValue())
                .ifPresent(refreshTokenRepository::delete);

        // TODO approvalStore
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token.getValue())
                .orElseThrow(() -> new InvalidTokenException("Could not find token=" + token.getValue()));
        String clientId = refreshToken.getClientId();
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(refreshToken.getClientId());
        if (clientDetails == null) {
            throw new InvalidTokenException("could not find client for token=" + token.getValue());
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put(CLIENT_ID, clientId);
        parameters.put(GRANT_TYPE, refreshToken.getGrantType());

        Authentication user = null;
        Collection<? extends GrantedAuthority> authorities;
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUsername());
        if (userDetails != null) {
            user = new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
            authorities = user.getAuthorities();
        } else {
            authorities = clientDetails.getAuthorities();
        }

        OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, clientDetails.getScope(),
                clientDetails.getResourceIds(), null, null, null);
        return new OAuth2Authentication(request, user);
    }
}
