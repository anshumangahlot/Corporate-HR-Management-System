package ui;

import models.User;
import javax.swing.*;


public abstract class Dashboard {
    // RBR
    public static final String APPLICATION_NAME = "HR Management System";

    protected JFrame frame;
    protected JPanel contentPanel;
    protected User currentUser;

    String dashboardScope = "package-private dashboard scope";

    private final String dashboardType;

    /**
     * Constructor for Dashboard
     * @param user - The user logged in
     */
    public Dashboard(User user) {
        this.currentUser = user;
        this.dashboardType = getClass().getSimpleName();
    }

    /**
     * Abstract method to initialize the dashboard
     * Must be implemented by subclasses
     */
    // RBR
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

    public String getDashboardType() {
        return dashboardType;
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