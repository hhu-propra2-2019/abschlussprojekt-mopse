package mops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@SpringBootApplication
@EnableJdbcAuditing
public class Material1Application {

    /**
     * Runs the SpringApplication.
     *
     * @param args application args
     */
    public static void main(String[] args) {
        SpringApplication.run(Material1Application.class, args);
    }
}
