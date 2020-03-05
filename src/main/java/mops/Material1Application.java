package mops;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Material1Application {

    /**
     * Runs the SpringApplication.
     *
     * @param args application args
     */
    public static void main(String[] args) {
        SpringApplication.run(Material1Application.class, args);
    }

    @Bean
    ApplicationRunner init(TestService service) {
        return args -> service.run();
    }
}
