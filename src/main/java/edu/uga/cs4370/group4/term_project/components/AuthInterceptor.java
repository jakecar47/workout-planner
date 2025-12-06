package edu.uga.cs4370.group4.term_project.components;

import edu.uga.cs4370.group4.term_project.services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Autowired
    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        // Public paths
        if (path.equals("/login") ||
            path.equals("/register") ||
            path.equals("/logout") ||
            path.startsWith("/css") ||
            path.startsWith("/js") ||
            path.startsWith("/images") ||
            path.startsWith("/favicon")) {

            return true;
        }

        // Protected paths
        if (!userService.isAuthenticated()) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}