package ui;

import models.User;
import javax.swing.*;

/**
 * Abstract Dashboard Class
 * Demonstrates inheritance and polymorphism (CO2)
 * Provides common functionality for all dashboard types
 */
public abstract class Dashboard {
    
    protected JFrame frame;
    protected JPanel contentPanel;
    protected User currentUser;

    /**
     * Constructor for Dashboard
     * @param user - The user logged in
     */
    public Dashboard(User user) {
        this.currentUser = user;
    }

    /**
     * Abstract method to initialize the dashboard
     * Must be implemented by subclasses
     */
    public abstract void initializeDashboard();

    /**
     * Abstract method to show dashboard
     */
    public abstract void showDashboard();

    /**
     * Get current user
     * @return Current user object
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get frame
     * @return JFrame object
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Logout functionality
     */
    public void logout() {
        if (frame != null) {
            frame.dispose();
        }
        new LoginUI();
    }

    /**
     * Change password functionality
     */
    public abstract void changePassword();
}