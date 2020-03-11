package mops.utils;

import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Setups the basic testing context.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ActiveProfiles("test")
public @interface TestContext {
}
