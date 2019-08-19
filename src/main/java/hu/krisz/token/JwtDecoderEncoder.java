package hu.krisz.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import java.security.Key;
import java.util.Map;

/**
 * Class to encode/decode a JWT.
 *
 * @author krisztian.toth on 13-8-2019
 */
public class JwtDecoderEncoder {

    private static final String AUDIENCE = "aud";
    private static final String USER_NAME = "user_name";
    private static final String AUTHORITIES = "authorities";
    private static final String SCOPE = "scope";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";

    private final Key keyToSign;
    private final Key keyToVerify;

    /**
     * A {@link JwtClaimsSetVerifier} which can be used to verify the claims of a token. By default it's a noop
     * implementation.
     */
    private JwtClaimsSetVerifier jwtClaimsSetVerifier = claims -> {
    };

    /**
     * Constructor for {@link JwtDecoderEncoder} when a secret key algorithm (e.g. HS256) was used to sign the JWT. In
     * this case we use the same key to sign and to verify the token.
     *
     * @param key the {@link Key} to sign/verify the JWT.
     */
    public JwtDecoderEncoder(Key key) {
        this.keyToSign = key;
        this.keyToVerify = key;
    }

    /**
     * Constructor for {@link JwtDecoderEncoder} when an asymmetric key algorithm (e.g. ES256) was used to sign the JWT.
     * In this case we use a {@link java.security.PrivateKey} to sign and a {@link java.security.PublicKey} to verify
     * the token.
     *
     * @param keyToSign   the {@link java.security.PrivateKey} to sign the JWT
     * @param keyToVerify the {@link java.security.PublicKey} to verify the JWT
     */
    public JwtDecoderEncoder(Key keyToSign, Key keyToVerify) {
        this.keyToSign = keyToSign;
        this.keyToVerify = keyToVerify;
    }

    /**
     * Setter for {@link JwtClaimsSetVerifier} which can be used to verify the claims of the JWT. By default it's a
     * noop implementation.
     *
     * @param jwtClaimsSetVerifier the {@link JwtClaimsSetVerifier} to set
     */
    public void setJwtClaimsSetVerifier(JwtClaimsSetVerifier jwtClaimsSetVerifier) {
        this.jwtClaimsSetVerifier = jwtClaimsSetVerifier;
    }

    /**
     * Decodes and verifies the JWT.
     * See possible exceptions at {@link JwtParser#parseClaimsJws(java.lang.String)} and
     * {@link JwtClaimsSetVerifier#verify(Map)}
     *
     * @param tokenValue the token
     * @return the decoded token
     * @see JwtParser#parseClaimsJws(java.lang.String)
     * @see JwtClaimsSetVerifier#verify(Map)
     */
    public Map<String, Object> decode(String tokenValue) {
        Claims claims = Jwts.parser()
                .setSigningKey(keyToVerify)
                .parseClaimsJws(tokenValue)
                .getBody();
        jwtClaimsSetVerifier.verify(claims);
        return claims;
    }

    public String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(accessToken.getValue())
                .setExpiration(accessToken.getExpiration())
                .claim(CLIENT_ID, authentication.isClientOnly())
                .claim(GRANT_TYPE, authentication.getOAuth2Request().getGrantType())
                .claim(SCOPE, accessToken.getScope())
                .claim(AUDIENCE, authentication.getOAuth2Request().getResourceIds());

        if (!authentication.isClientOnly()) {
            jwtBuilder
                    .claim(USER_NAME, authentication.getName())
                    .claim(AUTHORITIES, authentication.getUserAuthentication().getAuthorities());
        } else {
            jwtBuilder.claim(AUTHORITIES, authentication.getAuthorities());
        }

        return jwtBuilder
                .signWith(keyToSign)
                .compact();
    }
}
