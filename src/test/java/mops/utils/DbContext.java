package mops.utils;

import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

import java.lang.annotation.*;

/**
 * Setups a H2 test database which executes every query as a transaction and rolls it back afterwards.
 * Can be used to test aggregate saving and loading with
 * {@link org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest @DataJdbcTest}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestContext
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EnableJdbcAuditing
public @interface DbContext {
}
