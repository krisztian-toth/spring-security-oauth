package hu.krisz.token;

import hu.krisz.dao.entity.token.RefreshToken;
import hu.krisz.dao.repository.RefreshTokenRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.time.Instant;
import java.util.Date;

/**
 * JWT Token store which persists the refresh token.
 *
 * @author krisztian.toth on 8-8-2019
 */
public class PersistedRefreshTokenJwtTokenStore extends JwtTokenStore {

    private static final Log LOG = LogFactory.getLog(PersistedRefreshTokenJwtTokenStore.class);

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     *
     * @param jwtTokenEnhancer the {@link JwtAccessTokenConverter}
     */
    public PersistedRefreshTokenJwtTokenStore(JwtAccessTokenConverter jwtTokenEnhancer,
                                              RefreshTokenRepository refreshTokenRepository) {
        super(jwtTokenEnhancer);
        this.refreshTokenRepository = refreshTokenRepository;
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
                refreshTokenRepository.findByUsername(username).ifPresent(refreshTokenRepository::delete);

                Date expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
                refreshTokenRepository.save(new RefreshToken(refreshToken.getValue(), username, expiration.toInstant(),
                        Instant.now()));
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
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        refreshTokenRepository.findByToken(token.getValue())
                .ifPresent(refreshTokenRepository::delete);

        // TODO revoke approvals with approvalStore
    }
}
