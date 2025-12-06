package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /* ------------------------------------------------------
     * LOGIN PAGE
     * ------------------------------------------------------ */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model) {

        if (username == null || username.isBlank() || password.isBlank()) {
            model.addAttribute("error", "Username and password are required.");
            return "login";
        }

        boolean success = userService.login(username.trim(), password.trim());

        if (success) {
            return "redirect:/";     
        }

        model.addAttribute("error", "Invalid username or password.");
        return "login";
    }

    /* ------------------------------------------------------
     * REGISTER PAGE
     * ------------------------------------------------------ */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String username,
                               @RequestParam String password,
                               Model model) {

        if (email.isBlank() || username.isBlank() || password.isBlank()) {
            model.addAttribute("error", "All fields are required.");
            return "register";
        }

        boolean success = userService.registerUser(
                email.trim(),
                username.trim(),
                password.trim()
        );

        if (success) {
            return "redirect:/login";
        }

        model.addAttribute("error", "Email or username already exists.");
        return "register";
    }

    /* ------------------------------------------------------
     * LOGOUT
     * ------------------------------------------------------ */
    @GetMapping("/logout")
    public String logout() {
        userService.logout();
        return "redirect:/login";
    }
}
