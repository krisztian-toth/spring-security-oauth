package hu.krisz.config;

import hu.krisz.dao.repository.RefreshTokenRepository;
import hu.krisz.token.PersistedRefreshTokenJwtTokenStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Configuration class for setting up the token services.
 *
 * @author krisztian.toth on 7-8-2019
 */
@Configuration
public class TokenConfig {
    @Value("{jwt.signing.key}")
    private String jwtSigningKey;

    @Bean
    protected JwtAccessTokenConverter accessTokenConverterBean() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(jwtSigningKey);
        return jwtAccessTokenConverter;
    }

    @Bean
    protected TokenStore tokenStoreBean(JwtAccessTokenConverter jwtAccessTokenConverter,
                                    RefreshTokenRepository refreshTokenRepository) {
        return new PersistedRefreshTokenJwtTokenStore(jwtAccessTokenConverter, refreshTokenRepository);
    }

    @Bean
    protected DefaultTokenServices defaultTokenServicesBean(TokenStore tokenStore,
                                                        ClientDetailsService clientDetailsService) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        return defaultTokenServices;
    }
}
