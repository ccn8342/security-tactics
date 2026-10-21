package edu.rit.security_tactics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides REST endpoints representing application services with different
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
}
