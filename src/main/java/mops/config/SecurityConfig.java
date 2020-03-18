package mops.config;


import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * Configuration for spring security with keycloak.
     *
     * @param auth auto injected
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider authProvider = keycloakAuthenticationProvider();
        authProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(authProvider);
    }

    /**
     * provides a SessionAuthenticationStrategy.
     *
     * @return SessionAuthenticationStrategy
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(
                new SessionRegistryImpl());
    }

    /**
     * allows to @Autowire a Keycloak access token.
     *
     * @return Keycloak access token.
     */
    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @SuppressWarnings("PMD.LawOfDemeter")
    public AccessToken getAccessToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return ((KeycloakPrincipal<?>) request.getUserPrincipal()).getKeycloakSecurityContext().getToken();
    }

    /**
     * Configures spring security  that authentication
     * is required for all resources.
     *
     * @param http auto injected
     * @throws Exception on error
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        forceHTTPS(http);
        http.authorizeRequests()
                .antMatchers("/actuator/**")
                .hasRole("monitoring")
                .anyRequest()
                .authenticated();
        http
                .anonymous()
                .disable();
    }

    /**
     * Declaring this class enables us to use the Spring specific.
     * {@link org.springframework.security.access.annotation.Secured} annotation
     * or the JSR-250 Java Standard
     * {@link javax.annotation.security.RolesAllowed} annotation
     * for Role-based authorization
     */
    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
    public static class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    }

    /**
     * Redirect all Requests to SSL if header in proxy are set.
     *
     * @param http
     * @throws Exception
     */
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.LawOfDemeter"})
    private void forceHTTPS(HttpSecurity http) throws Exception {
        http.requiresChannel()
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure();
    }
}
