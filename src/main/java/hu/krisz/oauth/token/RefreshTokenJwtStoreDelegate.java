package hu.krisz.oauth.token;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * A class which passes the persistence of the refresh token to the delegated {@link TokenStore} implementation.
 *
 * @author krisztian.toth on 23-8-2019
 */
public class RefreshTokenJwtStoreDelegate extends JwtTokenStore {

    private final TokenStore delegateTokenStore;

    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     *
     * @param jwtTokenEnhancer the {@link JwtAccessTokenEnhancer}
     * @param delegateTokenStore the delegated {@link TokenStore} implementation
     */
    public RefreshTokenJwtStoreDelegate(JwtAccessTokenConverter jwtTokenEnhancer,
                                        TokenStore delegateTokenStore) {
        super(jwtTokenEnhancer);
        this.delegateTokenStore = delegateTokenStore;
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        delegateTokenStore.storeRefreshToken(refreshToken, authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return delegateTokenStore.readRefreshToken(tokenValue);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return delegateTokenStore.readAuthenticationForRefreshToken(token);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        delegateTokenStore.removeRefreshToken(token);
    }
}
