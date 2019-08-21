package hu.krisz.oauth.config;

import hu.krisz.oauth.clientdetails.EntityBasedClientDetailsService;
import hu.krisz.oauth.dao.repository.ClientRepository;
import hu.krisz.oauth.dao.repository.UserRepository;
import hu.krisz.oauth.userdetails.EntityBasedUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * Configuration class for the application.
 *
 * @author krisztian.toth on 7-8-2019
 */
@Configuration
public class ApplicationConfig {

    /**
     * Basically the strength of the password hash.
     */
    private static final int B_CRYPT_LOG_ROUNDS = 12;

    @Bean
    protected PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder(B_CRYPT_LOG_ROUNDS);
    }

    @Bean
    protected UserDetailsService userDetailsServiceBean(UserRepository userRepository) {
        return new EntityBasedUserDetailsService(userRepository);
    }

    @Bean
    protected ClientDetailsService clientDetailsServiceBean(ClientRepository clientRepository) {
        return new EntityBasedClientDetailsService(clientRepository);
    }
}
