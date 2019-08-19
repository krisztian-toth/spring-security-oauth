package hu.krisz.token;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts the access token to a JWT while not modifying the refresh token itself.
 *
 * @author krisztian.toth on 15-8-2019
 */
public class JwtAccessTokenEnhancer implements TokenEnhancer {

    private final JwtDecoderEncoder jwtDecoderEncoder;

    private static final String TOKEN_ID = "jti";

    public JwtAccessTokenEnhancer(JwtDecoderEncoder jwtDecoderEncoder) {
        this.jwtDecoderEncoder = jwtDecoderEncoder;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
        String tokenId = result.getValue();
        if (!info.containsKey(TOKEN_ID)) {
            info.put(TOKEN_ID, tokenId);
        }
        result.setAdditionalInformation(info);
        result.setValue(jwtDecoderEncoder.encode(accessToken, authentication));
        return result;
    }
}
