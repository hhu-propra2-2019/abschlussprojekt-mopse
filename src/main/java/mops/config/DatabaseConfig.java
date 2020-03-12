package mops.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

/**
 * Configuration for database.
 */
@Configuration
@EnableJdbcAuditing
public class DatabaseConfig {
}
