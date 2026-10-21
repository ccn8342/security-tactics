package edu.rit.security_tactics.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Verifies the role-based access rules for protected services. */
@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationTests {

    /** Mock MVC client used to request the protected service URLs. */
    @Autowired
    private MockMvc mvc;

    /**
     * Verifies that each role can access the services permitted to that role.
     *
     * @param role the role assigned to the authenticated test user
     * @param service the service URL the role is allowed to access
     * @throws Exception if the mock request cannot be performed
     */
    @ParameterizedTest
    @CsvSource({
            "USER, /api/user",
            "MANAGER, /api/user",
            "MANAGER, /api/manager",
            "ADMIN, /api/user",
            "ADMIN, /api/manager",
            "ADMIN, /api/admin"
    })
    void roleCanAccessAllowedService(String role, String service) throws Exception {
        mvc.perform(get(service).with(user("test-user").roles(role)))
                .andExpect(status().isOk());
    }

    /**
     * Verifies that an authenticated user receives HTTP 403 when its role does not
     * have permission to access a service.
     *
     * @param role the role assigned to the authenticated test user
     * @param service the service URL the role is not allowed to access
     * @throws Exception if the mock request cannot be performed
     */
    @ParameterizedTest
    @CsvSource({
            "USER, /api/manager",
            "USER, /api/admin",
            "MANAGER, /api/admin"
    })
    void roleCannotAccessRestrictedService(String role, String service) throws Exception {
        mvc.perform(get(service).with(user("test-user").roles(role)))
                .andExpect(status().isForbidden());
    }
}
