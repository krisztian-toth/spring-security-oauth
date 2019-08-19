package hu.krisz.config;

import hu.krisz.dao.repository.RefreshTokenRepository;
import hu.krisz.token.JwtAccessTokenEnhancer;
import hu.krisz.token.JwtDecoderEncoder;
import hu.krisz.token.PersistedRefreshTokenJwtTokenStore;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.security.Key;

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
    protected TokenEnhancer accessTokenEnhancerBean(JwtDecoderEncoder jwtDecoderEncoder) {
        return new JwtAccessTokenEnhancer(jwtDecoderEncoder);
    }

    @Bean
    protected JwtDecoderEncoder jwtDecoderEncoderBean(Key key) {
        return new JwtDecoderEncoder(key);
    }

    @Bean
    protected Key signingKeyBean() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256); // read from configuration
    }

    @Bean
    protected AccessTokenConverter accessTokenConverterBean() {
        return new DefaultAccessTokenConverter();
    }

    @Bean
    protected TokenStore tokenStoreBean(JwtDecoderEncoder jwtDecoderEncoder,
                                        AccessTokenConverter accessTokenConverter,
                                        RefreshTokenRepository refreshTokenRepository,
                                        ClientDetailsService clientDetailsService,
                                        UserDetailsService userDetailsService) {
        return new PersistedRefreshTokenJwtTokenStore(jwtDecoderEncoder, accessTokenConverter, refreshTokenRepository,
                clientDetailsService, userDetailsService);
    }

    @Bean
    @Primary
    protected AuthorizationServerTokenServices defaultTokenServicesBean(TokenStore tokenStore,
                                                                        ClientDetailsService clientDetailsServiceBean,
                                                                        TokenEnhancer accessTokenEnhancer) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setClientDetailsService(clientDetailsServiceBean);
        defaultTokenServices.setTokenEnhancer(accessTokenEnhancer);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setReuseRefreshToken(false);
        return defaultTokenServices;
    }
}
