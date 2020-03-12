package mops.utils;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

/**
 * Allows to use keycloak without starting the whole application.
 * For example when using {@link org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest @WebMvcTest}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestContext
@ComponentScan(basePackageClasses = { KeycloakSecurityComponents.class, KeycloakSpringBootConfigResolver.class })
public @interface KeycloakContext {
}
