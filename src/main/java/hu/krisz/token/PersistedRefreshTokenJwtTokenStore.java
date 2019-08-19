package hu.krisz.token;

import hu.krisz.dao.entity.token.RefreshToken;
import hu.krisz.dao.repository.RefreshTokenRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token store which persists the refresh token.
 *
 * @author krisztian.toth on 8-8-2019
 */
public class PersistedRefreshTokenJwtTokenStore implements TokenStore {

    private static final Log LOG = LogFactory.getLog(PersistedRefreshTokenJwtTokenStore.class);

    private static final String CLIENT_ID = "client_id";
    private static final String GRANT_TYPE = "grant_type";

    private final JwtDecoderEncoder jwtDecoderEncoder;
    private final AccessTokenConverter accessTokenConverter;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClientDetailsService clientDetailsService;
    private final UserDetailsService userDetailsService;

    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     * @param jwtDecoderEncoder a {@link JwtDecoderEncoder}
     * @param accessTokenConverter an {@link AccessTokenConverter}
     * @param refreshTokenRepository the {@link RefreshTokenRepository}
     * @param clientDetailsService the {@link ClientDetailsService}
     * @param userDetailsService the {@link UserDetailsService}
     */
    public PersistedRefreshTokenJwtTokenStore(JwtDecoderEncoder jwtDecoderEncoder,
                                              AccessTokenConverter accessTokenConverter,
                                              RefreshTokenRepository refreshTokenRepository,
                                              ClientDetailsService clientDetailsService,
                                              UserDetailsService userDetailsService) {
        this.jwtDecoderEncoder = jwtDecoderEncoder;
        this.accessTokenConverter = accessTokenConverter;
        this.refreshTokenRepository = refreshTokenRepository;
        this.clientDetailsService = clientDetailsService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return accessTokenConverter.extractAuthentication(jwtDecoderEncoder.decode(token.getValue()));
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return accessTokenConverter.extractAuthentication(jwtDecoderEncoder.decode(token));
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        // we don't store access tokens
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return accessTokenConverter.extractAccessToken(tokenValue, jwtDecoderEncoder.decode(tokenValue));

    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        // we don't store access tokens in any persistent store
    }

    /**
     * Stores the refresh token in the database.
     *
     * @param refreshToken The refresh token to store.
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
                if (principal instanceof String) {
                    username = (String) principal;
                } else if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername();
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

        // TODO add approvals with approvalStore
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        refreshTokenRepository.findByToken(token.getValue())
                .ifPresent(refreshTokenRepository::delete);

        // TODO revoke approvals with approvalStore
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        // we don't store access tokens in any persistent store
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        // We don't want to accidentally issue a token, and we have no way to reconstruct the refresh token
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return Collections.emptyList();
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return Collections.emptyList();
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
