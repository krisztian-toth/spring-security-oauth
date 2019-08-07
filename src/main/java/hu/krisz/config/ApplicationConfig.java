package hu.krisz.config;

import hu.krisz.dao.repository.UserRepository;
import hu.krisz.userdetails.EntityBasedUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for shared configuration
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
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(B_CRYPT_LOG_ROUNDS);
    }

    @Bean
    protected UserDetailsService userDetailsService(UserRepository userRepository) {
        return new EntityBasedUserDetailsService(userRepository);
    }
}
