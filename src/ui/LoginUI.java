package ui;

import services.AuthService;
import models.User;

import javax.swing.*;
import java.awt.*;

public class LoginUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Login");
        frame.setSize(350, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2, 5, 5));

        // Username
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        // Password
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        // Role Toggle
        JLabel roleLabel = new JLabel("Role:");

        JRadioButton adminBtn = new JRadioButton("Admin");
        JRadioButton empBtn = new JRadioButton("Employee");

        ButtonGroup group = new ButtonGroup();
        group.add(adminBtn);
        group.add(empBtn);
        adminBtn.setSelected(true);

        JPanel rolePanel = new JPanel(new FlowLayout());
        rolePanel.add(adminBtn);
        rolePanel.add(empBtn);

        // Login Button
        JButton loginBtn = new JButton("Login");

        // Add components
        frame.add(userLabel);
        frame.add(userField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(roleLabel);
        frame.add(rolePanel);
        frame.add(new JLabel());
        frame.add(loginBtn);

        // Action
        loginBtn.addActionListener(e -> {

            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields");
                return;
            }

            String selectedRole = adminBtn.isSelected() ? "admin" : "employee";

            User user = AuthService.loginUser(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(frame, "Invalid credentials");
            } 
            else if (!user.getRole().equalsIgnoreCase(selectedRole)) {
                JOptionPane.showMessageDialog(frame, "Role mismatch!");
            } 
            else {
                frame.dispose();

                if (user.getRole().equalsIgnoreCase("admin")) {
                    new AdminDashboard(user);
                } else {
                    new EmployeeDashboard(user);
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}