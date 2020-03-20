package mops.utils;

import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Setups the basic testing context.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs("doc/api/generated-snippets")
public @interface TestContext {
}
