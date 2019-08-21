package hu.krisz.oauth.config;

import hu.krisz.oauth.dao.repository.RefreshTokenRepository;
import hu.krisz.oauth.token.JwtAccessTokenEnhancer;
import hu.krisz.oauth.token.PersistedRefreshTokenJwtTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.util.Collections;

/**
 * Configuration class for setting up the token services.
 *
 * @author krisztian.toth on 7-8-2019
 */
@Configuration
public class TokenConfig {
    @Value("{jwt.signing.key}")
    private String jwtSigningKey;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    protected TokenEnhancer accessTokenEnhancerBean(JwtAccessTokenConverter accessTokenConverter) {
        return new JwtAccessTokenEnhancer(accessTokenConverter);
    }


    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverterBean() {
        return new JwtAccessTokenConverter(); // set verifier and signing key explicitly
    }

    @Bean
    protected TokenStore tokenStoreBean(JwtAccessTokenConverter jwtAccessTokenConverter,
                                        RefreshTokenRepository refreshTokenRepository,
                                        ClientDetailsService clientDetailsService) {
        return new PersistedRefreshTokenJwtTokenStore(jwtAccessTokenConverter, refreshTokenRepository,
                clientDetailsService, userDetailsService);
    }

    @Bean
    @Primary
    protected AuthorizationServerTokenServices defaultTokenServicesBean(TokenStore tokenStore,
                                                                        ClientDetailsService clientDetailsServiceBean,
                                                                        TokenEnhancer accessTokenEnhancerBean) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setAuthenticationManager(createPreAuthProvider());
        defaultTokenServices.setClientDetailsService(clientDetailsServiceBean);
        defaultTokenServices.setTokenEnhancer(accessTokenEnhancerBean);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setReuseRefreshToken(false);
        return defaultTokenServices;
    }

    private ProviderManager createPreAuthProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        return new ProviderManager(Collections.singletonList(provider));
    }

}
