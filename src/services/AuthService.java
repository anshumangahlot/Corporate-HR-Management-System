package services;

import db.DBConnection;
import models.User;
import exceptions.AuthenticationException;
import exceptions.DatabaseException;
import exceptions.ValidationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Authentication Service
 * Handles user login with comprehensive error handling
 * Demonstrates CO3 - Error handling requirement
 */
public class AuthService {

    /**
     * Authenticates user with username and password
     * @param username - User's username
     * @param password - User's password
     * @return User object if authentication successful
     * @throws AuthenticationException if credentials are incorrect
     * @throws ValidationException if input is invalid
     * @throws DatabaseException if database operation fails
     */
    public static User loginUser(String username, String password) 
            throws AuthenticationException, ValidationException, DatabaseException {

        // RBR
        // Validation
        try {
            validateCredentials(username, password);
        } catch (ValidationException e) {
            throw e;
        }

        // DRL-Select
        String query = "SELECT id, username, role FROM users WHERE username=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    password,
                    rs.getString("role")
                );
                return user;
            } else {
                throw new AuthenticationException("Invalid username or password");
            }

        } catch (AuthenticationException e) {
            // RBR
            throw e;
        } catch (java.sql.SQLException e) {
            // RBR
            throw new DatabaseException("Database error during login: " + e.getMessage(), e);
        } catch (Exception e) {
            // RBR
            throw new DatabaseException("Unexpected error during login: " + e.getMessage(), e);
        }
    }

    /**
     * Validates user credentials
     * @param username - Username to validate
     * @param password - Password to validate
     * @throws ValidationException if credentials are invalid
     */
    private static void validateCredentials(String username, String password) 
            throws ValidationException {
        
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        if (username.length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long");
        }
        if (password.length() < 3) {
            throw new ValidationException("Password must be at least 3 characters long");
        }
    }
}
