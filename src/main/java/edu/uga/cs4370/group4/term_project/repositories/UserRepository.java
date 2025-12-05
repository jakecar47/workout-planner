package edu.uga.cs4370.group4.term_project.repositories;

import edu.uga.cs4370.group4.term_project.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class UserRepository {

    @Autowired
    private DataSource dataSource;

    // CREATE USER
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (email, uname, password) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUname());
            stmt.setString(3, user.getPassword());

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    // FIND BY EMAIL
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("uname"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toString()   
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding user by email: " + e.getMessage());
        }

        return null;
    }

    // FIND BY ID
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("uname"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toString()   
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding user by ID: " + e.getMessage());
        }

        return null;
    }

    // CHECK EMAIL EXISTS
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error checking email existence: " + e.getMessage());
        }

        return false;
    }

    // CHECK USERNAME EXISTS
    public boolean usernameExists(String uname) {
        String sql = "SELECT id FROM users WHERE uname = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uname);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error checking username existence: " + e.getMessage());
        }

        return false;
    }
}
