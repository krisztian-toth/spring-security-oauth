package hu.krisz.oauth.token;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Converts the access token to a JWT while not modifying the refresh token itself.
 *
 * @author krisztian.toth on 15-8-2019
 */
public class JwtAccessTokenEnhancer implements TokenEnhancer {

    private final JwtAccessTokenConverter accessTokenConverter;

    public JwtAccessTokenEnhancer(JwtAccessTokenConverter accessTokenConverter) {
        this.accessTokenConverter = accessTokenConverter;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2AccessToken enhancedAccessToken = accessTokenConverter.enhance(accessToken, authentication);
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(enhancedAccessToken);
        result.setRefreshToken(accessToken.getRefreshToken());
        return result;
    }
}
