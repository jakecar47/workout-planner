package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

@Service
public class UserService {

    private final DataSource dataSource;
    private User loggedInUser;

    @Autowired
    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
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
            ps.setString(3, password);

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

                String storedPassword = rs.getString("password");
                if (!storedPassword.equals(password)) return false;

                loggedInUser = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("uname"),
                        storedPassword,
                        rs.getTimestamp("created_at").toString()   
                );

                return true;
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
        loggedInUser = null;
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
