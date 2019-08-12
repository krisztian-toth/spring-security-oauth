package hu.krisz.token;

import hu.krisz.dao.entity.token.RefreshToken;
import hu.krisz.dao.repository.RefreshTokenRepository;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * JWT Token store which persists the refresh token.
 *
 * @author krisztian.toth on 8-8-2019
 */
public class PersistedRefreshTokenJwtTokenStore extends JwtTokenStore {

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
        String tokenValue = refreshToken.getValue();
        refreshTokenRepository.save(new RefreshToken(extractTokenKey(tokenValue), tokenValue));
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return refreshTokenRepository.findById(extractTokenKey(tokenValue))
                .map(token -> super.readRefreshToken(token.getToken()))
                .orElse(null);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        refreshTokenRepository.findById(extractTokenKey(token.getValue()))
                .ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Generates a key from the token. Lifted from
     * {@link org.springframework.security.oauth2.provider.token.store.JdbcTokenStore}.
     *
     * @param value the token's value
     * @return the key generated from the token
     */
    @SuppressWarnings({"squid:S1166", "squid:S2070"})
    private String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        String payload;
        try {
            payload = value.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("payload is not a valid JWT string", e);
        }

        byte[] bytes = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
        return String.format("%032x", new BigInteger(1, bytes));
    }
}
