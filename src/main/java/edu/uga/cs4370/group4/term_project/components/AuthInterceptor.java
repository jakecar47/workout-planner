package edu.uga.cs4370.group4.term_project.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import edu.uga.cs4370.group4.term_project.services.UserService;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // handle user services 
    private UserService userService;

    /**
     * Injects a UserService object as a dependency for 
     * AuthInterceptor as it is initialized. AuthInterceptor is also
     * initialized by SpringBoot for inversion of control.
     */
    @Autowired
    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    /**
     * Checks if the user is authenticated before allowing access to
     * protected routes. If not authenticated, redirects to login page.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
    HttpServletResponse response, Object handler) throws Exception {
            if (!userService.isAuthenticated()) {
            // Redirect the user to login page
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}