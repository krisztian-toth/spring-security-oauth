package hu.krisz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration for the data source.
 *
 * @author krisztian.toth on 5-8-2019
 */
@Configuration
public class DatasourceConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassName;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    protected DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName(dbDriverClassName)
                .url(datasourceUrl)
                .username(dbUsername)
                .password(dbPassword)
                .build();
    }
}
