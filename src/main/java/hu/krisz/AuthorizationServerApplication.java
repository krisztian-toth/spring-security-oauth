package hu.krisz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Entry point for the Authorization Server.
 *
 * @author krisztian.toth on 5-8-2019
 */
@SpringBootApplication(scanBasePackages = {"hu.krisz.config"})
@EnableResourceServer
public class AuthorizationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }
}
