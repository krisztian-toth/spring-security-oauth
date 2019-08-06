package hu.krisz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Authorization Server.
 *
 * @author krisztian.toth on 5-8-2019
 */
@SpringBootApplication(scanBasePackages = {"hu.krisz.config"})
public class AuthorizationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }
}
