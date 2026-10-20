package edu.rit.security_tactics.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This test class verifies the config of the in-memory user store and password encoding.
 * It checks that the demo accounts have encoded passwords, distinct roles, and that unknown users cannot be loaded.
 */
@SpringBootTest
class SecurityConfigTests {

    @Autowired
    private UserDetailsService users; // This is the in-memory user store configured in SecurityConfig.

    @Autowired
    private PasswordEncoder passwordEncoder; // This is the password encoder bean configured in SecurityConfig.

    /**
     * Tests that the demo accounts have encoded passwords and distinct roles.
     * for each demo account, it verifies that the stored password is not equal to the plain text password,
     * that the password encoder can correctly match the plain text password to the encoded password,
     * and that the user has the expected authority.
     * @param username
     * @param password
     * @param authority
     */
    @ParameterizedTest
    @CsvSource({
            "alice, AliceDemo123!, ROLE_USER",
            "morgan, MorganDemo123!, ROLE_MANAGER",
            "alex, AlexDemo123!, ROLE_ADMIN"
    })
    void demoAccountsHaveEncodedPasswordsAndDistinctRoles(
            String username, String password, String authority) {
        UserDetails user = users.loadUserByUsername(username);

        assertThat(user.getPassword()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("incorrect-password", user.getPassword())).isFalse();
        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(authority);
    }

    /**
     * Tests that an unknown user cannot be loaded from the in-memory user store.
     * It verifies that attempting to load a user with a username that does not exist in the store
     * throws a UsernameNotFoundException.
     */
    @Test
    void unknownUserCannotBeLoaded() {
        assertThatThrownBy(() -> users.loadUserByUsername("unknown-user"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
