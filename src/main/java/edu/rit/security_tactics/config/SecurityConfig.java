package edu.rit.security_tactics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * This configuration class sets up an in-memory user store with three demo users, each one
 * with different authorization levels. It also provides a password encoder bean for hashing passwords.
 * These users are stored in memory for demonstration purposes.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // These are public demo credentials, the account store holds their BCrypt hashes.
        UserDetails alice = User.withUsername("alice")
                .password(passwordEncoder.encode("AliceDemo123!"))
                .roles("USER")
                .build();

        UserDetails morgan = User.withUsername("morgan")
                .password(passwordEncoder.encode("MorganDemo123!"))
                .roles("MANAGER")
                .build();

        UserDetails alex = User.withUsername("alex")
                .password(passwordEncoder.encode("AlexDemo123!"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(alice, morgan, alex);
    }
}
