package edu.rit.security_tactics.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides REST endpoitns representating application services with different
 * intended levels of access.
*/
@RestController
public class ServiceController {
    @GetMapping("/api/public")
    public String publicService() {
        return "Public service: Accessible to everyone.";
    }

    @GetMapping("/api/user")
    public String userService() {
        return "User service: Accessible to authenticated users.";
    }

    @GetMapping("/api/manager")
    public String managerService() {
        return "Manager service: Restricted to managers and administrators.";
    }

    @GetMapping("/api/admin")
    public String adminService() {
        return "Admin service: Restricted to administrators.";
    }

    /**
     * Provides navigation options when an authenticated user is denied access.
     *
     * @return a small HTML access-denied page
     */
    @GetMapping(value = "/access-denied", produces = MediaType.TEXT_HTML_VALUE)
    public String accessDenied() {
        return """
                <!doctype html>
                <html lang="en">
                <head><meta charset="utf-8"><title>Access Denied</title></head>
                <body>
                    <h1>Access Denied</h1>
                    <p>Your account does not have permission to access that service.</p>
                    <nav>
                        <a href="/api/public">Public service</a> |
                        <a href="/api/user">User service</a> |
                        <a href="/api/manager">Manager service</a> |
                        <a href="/api/admin">Admin service</a> |
                        <a href="/logout">Log out</a>
                    </nav>
                </body>
                </html>
                """;
    }
}
