package edu.rit.security_tactics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This configuration class sets up an in-memory user store with three demo users, each one
 * with different authorization levels. It also provides a password encoder bean for hashing passwords.
 * These users are stored in memory for demonstration purposes.
 */
@Configuration
public class SecurityConfig {

    // could be extracted to enum
    public final String ROLE_USER = "USER";
    public final String ROLE_MANAGER = "MANAGER";
    public final String ROLE_ADMIN = "ADMIN";

    final String[] ROUTES_PUBLIC = new String[] {
            "/api/public"
    };

    // this is currently unused since routes are protected by default. But it could be used in the future.
    final String[] ROUTES_USERS = new String[] {
            "/api/user"
    };

    final String[] ROUTES_MANAGERS = new String[] {
            "/api/manager"
    };

    final String[] ROUTES_ADMINS = new String[] {
            "/api/admin"
    };

    /**
     * Configures the security filter chain for the application.
     * It defines the authorization rules for different endpoints and sets up form-based login.
     * The "/api/public" endpoint is accessible to everyone, while other endpoints require authentication.
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(ROUTES_PUBLIC).permitAll()
                .requestMatchers(ROUTES_MANAGERS).hasAnyRole(ROLE_MANAGER, ROLE_ADMIN)
                .requestMatchers(ROUTES_ADMINS).hasRole(ROLE_ADMIN).anyRequest().authenticated()
        )
        .formLogin(form -> form
                .defaultSuccessUrl("/api/user")
                .permitAll())
        .logout(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Provides a password encoder bean that uses BCrypt hashing algorithm.
     * This encoder is used to hash passwords for the in-memory users store.
     * @return a PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures an in-memory user store with three demo users: Alice, Morgan, and Alex.
     * Each user has a unique username, password, and role. The passwords are hashed using the provided PasswordEncoder.
     * @param passwordEncoder the PasswordEncoder to hash the passwords
     * @return a UserDetailsService that manages the in-memory users
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // These are public demo credentials, the account store holds their BCrypt hashes.
        UserDetails alice = User.withUsername("alice")
                .password(passwordEncoder.encode("AliceDemo123!"))
                .roles(ROLE_USER)
                .build();

        UserDetails morgan = User.withUsername("morgan")
                .password(passwordEncoder.encode("MorganDemo123!"))
                .roles(ROLE_MANAGER)
                .build();

        UserDetails alex = User.withUsername("alex")
                .password(passwordEncoder.encode("AlexDemo123!"))
                .roles(ROLE_ADMIN)
                .build();

        return new InMemoryUserDetailsManager(alice, morgan, alex);
    }
}
