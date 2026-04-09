package models;

/**
 * User Model Class
 * Implements IEmployee interface demonstrating interface implementation (CO2)
 * Represents a user in the HR Management System with proper encapsulation
 */
public class User implements IEmployee {
    private int id;
    private String username;
    private String password;
    private String role;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters with private access specifiers
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    // ========== IEmployee Interface Implementations (CO2) ==========

    @Override
    public String getEmployeeInfo() {
        return "User: " + username + " | ID: " + id + " | Role: " + role;
    }

    @Override
    public boolean applyLeave(int days) {
        if (days > 0) {
            System.out.println(username + " applied for " + days + " days leave");
            return true;
        }
        return false;
    }

    @Override
    public double viewAttendance() {
        // Placeholder implementation - would fetch from database
        return 85.5; // Default 85.5% attendance
    }

    @Override
    public String viewPayroll() {
        // Placeholder implementation - would fetch from database
        return "Monthly Salary: Based on role and designation";
    }

    @Override
    public String getDepartment() {
        // Placeholder implementation - would fetch from database
        return "Department TBD";
    }

}
