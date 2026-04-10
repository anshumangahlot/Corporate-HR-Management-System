package ui;

import services.AuthService;
import models.User;
import exceptions.AuthenticationException;
import exceptions.ValidationException;
import exceptions.DatabaseException;

import javax.swing.*;
import java.awt.*;

public class LoginUI {

    public LoginUI() {
        initializeLoginUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }

    private void initializeLoginUI() {

        JFrame frame = new JFrame("HR Management System - Login");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Main content panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(500, 100));
        
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 0;
        headerGbc.weightx = 1.0;
        headerGbc.anchor = GridBagConstraints.CENTER;
        
        JLabel titleLabel = new JLabel("HR Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Login Portal");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerGbc.gridy = 0;
        headerPanel.add(titleLabel, headerGbc);
        headerGbc.gridy = 1;
        headerPanel.add(subtitleLabel, headerGbc);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        userLabel.setForeground(new Color(44, 62, 80));
        JTextField userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 12));
        userField.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        userField.setPreferredSize(new Dimension(300, 35));

        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(userField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passLabel.setForeground(new Color(44, 62, 80));
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 12));
        passField.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        passField.setPreferredSize(new Dimension(300, 35));

        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(passField, gbc);

        // Role Selection
        JLabel roleLabel = new JLabel("Login as:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roleLabel.setForeground(new Color(44, 62, 80));

        JRadioButton adminBtn = new JRadioButton("Administrator", true);
        adminBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        adminBtn.setBackground(Color.WHITE);
        adminBtn.setForeground(new Color(44, 62, 80));

        JRadioButton empBtn = new JRadioButton("Employee", false);
        empBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        empBtn.setBackground(Color.WHITE);
        empBtn.setForeground(new Color(44, 62, 80));

        ButtonGroup group = new ButtonGroup();
        group.add(adminBtn);
        group.add(empBtn);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.setBackground(Color.WHITE);
        rolePanel.add(adminBtn);
        rolePanel.add(empBtn);

        gbc.gridy = 4;
        formPanel.add(roleLabel, gbc);
        gbc.gridy = 5;
        formPanel.add(rolePanel, gbc);

        // Login Button
        JButton loginBtn = new JButton("LOG IN");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 13));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(46, 204, 113));
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(300, 45));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 6;
        gbc.insets = new Insets(25, 0, 0, 0);
        formPanel.add(loginBtn, gbc);
        
        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(236, 240, 241));
        footerPanel.setPreferredSize(new Dimension(500, 60));
        JLabel footerLabel = new JLabel("Secure Employee Management Portal");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        footerLabel.setForeground(new Color(127, 140, 141));
        footerPanel.add(footerLabel);

        // Add panels to main
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);

        // RBR
        loginBtn.addActionListener(e -> {

            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            try {
                // Attempt login with error handling
                User user = AuthService.loginUser(username, password);

                String selectedRole = adminBtn.isSelected() ? "admin" : "employee";

                if (!user.getRole().equalsIgnoreCase(selectedRole)) {
                    JOptionPane.showMessageDialog(frame, 
                        "Selected role does not match your account role", 
                        "Role Mismatch", 
                        JOptionPane.WARNING_MESSAGE);
                } else {
                    frame.dispose();

                    if (user.getRole().equalsIgnoreCase("admin")) {
                        new AdminDashboard(user);
                    } else {
                        new EmployeeDashboard(user);
                    }
                }

            } catch (ValidationException ve) {
                // RBR
                // Handle validation errors
                JOptionPane.showMessageDialog(frame, 
                    "Validation Error: " + ve.getMessage(), 
                    "Input Validation Failed", 
                    JOptionPane.ERROR_MESSAGE);

            } catch (AuthenticationException ae) {
                // RBR
                // Handle authentication errors
                JOptionPane.showMessageDialog(frame, 
                    "Authentication Failed: " + ae.getMessage(), 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);

            } catch (DatabaseException de) {
                // RBR
                // Handle database errors
                JOptionPane.showMessageDialog(frame, 
                    "Database Error: " + de.getMessage(), 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);

            } catch (Exception ex) {
                // Handle unexpected errors
                JOptionPane.showMessageDialog(frame, 
                    "Unexpected error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
