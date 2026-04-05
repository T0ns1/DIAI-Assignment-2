package pt.unl.fct.iadi.bookstore.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.context.SecurityContextHolderFilter

@Configuration
@EnableMethodSecurity
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic",
)
@SecurityScheme(
    name = "apiToken",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.HEADER,
    paramName = ApiTokenFilter.API_TOKEN_HEADER,
)
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        apiTokenFilter: ApiTokenFilter,
        requestAuditLoggingFilter: RequestAuditLoggingFilter,
        securityErrorHandlers: SecurityErrorHandlers,
    ): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling {
                it.authenticationEntryPoint(securityErrorHandlers)
                it.accessDeniedHandler(securityErrorHandlers)
            }
            .authorizeHttpRequests {
                it.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                it.requestMatchers(HttpMethod.POST, "/**").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/**").authenticated()
                it.requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                it.anyRequest().permitAll()
            }
            .addFilterAfter(requestAuditLoggingFilter, SecurityContextHolderFilter::class.java)
            .addFilterBefore(apiTokenFilter, BasicAuthenticationFilter::class.java)
            .build()

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService =
        InMemoryUserDetailsManager(
            User.withUsername("editor1").password(passwordEncoder.encode("editor1pass")).roles("EDITOR").build(),
            User.withUsername("editor2").password(passwordEncoder.encode("editor2pass")).roles("EDITOR").build(),
            User.withUsername("admin").password(passwordEncoder.encode("adminpass")).roles("ADMIN").build(),
        )

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}
