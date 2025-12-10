package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.annotation.SessionScope;

import javax.sql.DataSource;
import java.sql.*;

@Service
@SessionScope
public class UserService {

    private final DataSource dataSource;
    private User loggedInUser = null;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /** ---------------------------
     *  REGISTER A NEW USER
     * --------------------------- */
    public boolean registerUser(String email, String username, String password) {
        String sql = "INSERT INTO users (email, uname, password) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, username);
            ps.setString(3, passwordEncoder.encode(password));

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    /** ---------------------------
     *  LOGIN
     * --------------------------- */
    public boolean login(String username, String password) {
        String sql = "SELECT id, email, uname, password, created_at FROM users WHERE uname = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return false;

                String storedPasswordHash = rs.getString("password");
                boolean isPassMatch = passwordEncoder.matches(password, storedPasswordHash);
                if (!isPassMatch) return false;

                loggedInUser = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("uname"),
                        null,
                        rs.getTimestamp("created_at").toString()   
                );

                return isPassMatch;
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    /** ---------------------------
     *  LOGOUT
     * --------------------------- */
    public void logout() {
        this.loggedInUser = null;
    }

    /** ---------------------------
     *  GET LOGGED-IN USER
     * --------------------------- */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /** ---------------------------
     *  AUTH CHECK
     * --------------------------- */
    public boolean isAuthenticated() {
        return loggedInUser != null;
    }

    /** ---------------------------
     *  FIND USER BY ID
     * --------------------------- */
    public User getUserById(int userId) {
        String sql = "SELECT id, email, uname, password, created_at FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return null;

                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("uname"),
                        rs.getString("password"),
                        rs.getTimestamp("created_at").toString()   
                );
            }

        } catch (SQLException e) {
            System.out.println("getUserById failed: " + e.getMessage());
            return null;
        }
    }
}
