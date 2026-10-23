package edu.rit.security_tactics.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * These tests verify the authentication and authorization behavior of the application.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTests {

    /** This is the mock MVC object used to perform HTTP requests and verify responses in the tests. */
    @Autowired
    private MockMvc mvc;

    /**
     * Tests that the public service endpoint is accessible without authentication.
     * It performs a GET request to the /api/public endpoint and expects a 200 OK status
     * and that the request is unauthenticated.
     */
    @Test
    void publicServiceDoesNotRequireLogin() throws Exception {
        mvc.perform(get("/api/public"))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    /**
     * Tests that the login page is available without authentication.
     * It performs a GET request to the /login endpoint and expects a 200 OK status.
     */
    @Test
    void loginPageIsAvailableWithoutAuthentication() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    /**
     * Tests that protected service endpoints redirect to the login page when accessed without authentication.
     * It performs GET requests to the specified service endpoints and expects a 3xx redirection status
     * and that the redirection URL is /login.
     * @param service the protected service endpoint to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"/api/user", "/api/manager", "/api/admin"})
    void protectedServicesRedirectToLogin(String service) throws Exception {
        mvc.perform(get(service))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Tests that demo users can log in with valid credentials and reuse their session for subsequent requests.
     * It performs a form login with the specified username and password, expects successful authentication,
     * and then uses the obtained session to access the /api/user endpoint, which verifies that the user is still authenticated.
     * @param username the username of the demo user
     * @param password the password of the demo user
     * @param role the expected role of the demo user
     */
    @ParameterizedTest
    @CsvSource({
            "alice, AliceDemo123!, USER",
            "morgan, MorganDemo123!, MANAGER",
            "alex, AlexDemo123!, ADMIN"
    })
    void demoUsersCanLoginAndReuseTheirSession(
            String username, String password, String role) throws Exception {
        MvcResult login = mvc.perform(formLogin().user(username).password(password))
                .andExpect(authenticated().withUsername(username).withRoles(role))
                .andExpect(redirectedUrl("/api/user"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertThat(session).isNotNull();

        // Only the session is sent here, the username and password are not resubmitted.
        mvc.perform(get("/api/user").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("User service: Accessible to authenticated users."))
                .andExpect(authenticated().withUsername(username));
    }

    /**
     * Tests that invalid credentials do not authenticate the user and redirect to the login page with an error.
     * It performs a form login with the specified invalid username and password, expects unauthenticated status,
     * and verifies that the redirection URL is /login?error.
     * @param username the invalid username to test
     * @param password the invalid password to test
     */
    @ParameterizedTest
    @CsvSource({
            "alice, incorrect-password",
            "unknown-user, AliceDemo123!"
    })
    void invalidCredentialsDoNotAuthenticate(String username, String password) throws Exception {
        mvc.perform(formLogin().user(username).password(password))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    /**
     * Tests that a login attempt without a CSRF token is rejected with a 403 Forbidden status.
     * It performs a POST request to the /login endpoint with valid credentials but without including a CSRF token,
     * and expects a 403 Forbidden status and that the request is unauthenticated.
     */
    @Test
    void loginWithoutCsrfTokenIsRejected() throws Exception {
        mvc.perform(post("/login")
                        .param("username", "alice")
                        .param("password", "AliceDemo123!"))
                .andExpect(status().isForbidden())
                .andExpect(unauthenticated());
    }

    /**
     * Tests that logging out invalidates the authenticated session and redirects to the login page with a logout message.
     * It performs a form login to authenticate a user, retrieves the session, and then performs a POST request to the /logout endpoint
     * with the session and a CSRF token. It expects unauthenticated status, redirection to /login?logout, and verifies that the session is invalidated.
     * Finally, it attempts to access the /api/user endpoint without a valid session and expects redirection to the login page.
     */
    @Test
    void logoutInvalidatesTheAuthenticatedSession() throws Exception {
        MvcResult login = mvc.perform(formLogin().user("alice").password("AliceDemo123!"))
                .andExpect(authenticated().withUsername("alice"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mvc.perform(post("/logout").session(session).with(csrf()))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?logout"));

        assertThat(session.isInvalid()).isTrue();
        mvc.perform(get("/api/user"))
                .andExpect(redirectedUrl("/login"));
    }
}
