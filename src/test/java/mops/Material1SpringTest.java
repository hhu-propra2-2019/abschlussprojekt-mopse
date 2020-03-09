package mops;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

/**
 * Our Test Annotation for Spring Boot Tests. Setups the context (including H2 Database).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = { TestConfig.class, Material1Application.class })
public @interface Material1SpringTest {
}
