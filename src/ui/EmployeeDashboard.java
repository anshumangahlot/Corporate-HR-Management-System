package ui;

import db.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EmployeeDashboard {

    private JFrame frame;
    private JPanel contentPanel;
    private User currentUser;

    public EmployeeDashboard(User user) {
        this.currentUser = user;

        frame = new JFrame("Employee Dashboard");
        frame.setSize(1200, 750);
        frame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("HR Management System", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Employee Portal - Welcome, " + user.getUsername(), SwingConstants.RIGHT);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 235, 247));

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(subtitle, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        JPanel side = new JPanel(new GridLayout(11, 1, 5, 5));
        side.setBackground(new Color(44, 62, 80));
        side.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton profileBtn = new JButton("Profile");
        JButton deptBtn = new JButton("Department & Role");
        JButton attendBtn = new JButton("Attendance");
        JButton leaveBtn = new JButton("Apply Leave");
        JButton leaveStatusBtn = new JButton("Leave Status");
        JButton payrollBtn = new JButton("Payroll");
        JButton projectBtn = new JButton("Projects");
        JButton meetingBtn = new JButton("Meetings");
        JButton passBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        side.add(profileBtn);
        side.add(deptBtn);
        side.add(attendBtn);
        side.add(leaveBtn);
        side.add(leaveStatusBtn);
        side.add(payrollBtn);
        side.add(projectBtn);
        side.add(meetingBtn);
        side.add(passBtn);
        side.add(logoutBtn);

        JButton[] navButtons = {
            profileBtn, deptBtn, attendBtn, leaveBtn, leaveStatusBtn,
            payrollBtn, projectBtn, meetingBtn, passBtn
        };
        for (JButton navBtn : navButtons) {
            navBtn.setFont(new Font("Arial", Font.BOLD, 12));
            navBtn.setBackground(new Color(52, 152, 219));
            navBtn.setForeground(Color.WHITE);
            navBtn.setBorderPainted(false);
            navBtn.setFocusPainted(false);
            navBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        frame.add(side, BorderLayout.WEST);

        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(236, 240, 241));
        frame.add(contentPanel, BorderLayout.CENTER);

        profileBtn.addActionListener(e -> showProfile());
        deptBtn.addActionListener(e -> showDepartment());
        attendBtn.addActionListener(e -> showAttendance());
        leaveBtn.addActionListener(e -> applyLeave());
        leaveStatusBtn.addActionListener(e -> showLeaveStatus());
        payrollBtn.addActionListener(e -> showPayroll());
        projectBtn.addActionListener(e -> showProjects());
        meetingBtn.addActionListener(e -> showMeetings());
        passBtn.addActionListener(e -> changePassword());
        logoutBtn.addActionListener(e -> logout());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showProfile();
    }

    private int getEmployeeId() {
        int mappedId = currentUser.getId();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM Employee WHERE EmpID = ?")) {
            ps.setInt(1, mappedId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mappedId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String username = currentUser.getUsername().trim().toLowerCase();
        if (username.startsWith("emp")) {
            try {
                return Integer.parseInt(username.substring(3));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    private int getNextAttendanceId(Connection con) throws SQLException {
        String query = "SELECT COALESCE(MAX(att_id), 0) + 1 AS next_id FROM attendance_log";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("next_id");
        }
        return 1;
    }

    private void showProfile() {
        contentPanel.removeAll();
        contentPanel.setBorder(null);
        contentPanel.setLayout(new BorderLayout());

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT EmpID, Emp_name, DOB, Gender, Email, Street FROM Employee WHERE EmpID = ?"
            );
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JPanel headerPanel = new JPanel();
                headerPanel.setBackground(new Color(41, 128, 185));
                JLabel nameLabel = new JLabel(rs.getString("Emp_name"));
                nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
                nameLabel.setForeground(Color.WHITE);
                headerPanel.add(nameLabel);

                JPanel profilePanel = new JPanel(new GridLayout(0, 2, 15, 15));
                profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                profilePanel.setBackground(Color.WHITE);

                addProfileField(profilePanel, "Employee ID:", String.valueOf(rs.getInt("EmpID")));
                addProfileField(profilePanel, "Name:", rs.getString("Emp_name"));
                addProfileField(profilePanel, "Gender:", rs.getString("Gender"));
                addProfileField(profilePanel, "DOB:", rs.getDate("DOB") != null ? rs.getDate("DOB").toString() : "N/A");
                addProfileField(profilePanel, "Email:", rs.getString("Email"));
                addProfileField(profilePanel, "Street:", rs.getString("Street"));

                PreparedStatement phonePs = con.prepareStatement("SELECT Phone_Number FROM Employee_Phones WHERE EmpID = ? LIMIT 1");
                phonePs.setInt(1, empId);
                ResultSet phoneRs = phonePs.executeQuery();
                String phone = "Not available";
                if (phoneRs.next()) {
                    phone = phoneRs.getString("Phone_Number");
                }
                addProfileField(profilePanel, "Phone:", phone);

                JPanel scrollPanel = new JPanel(new BorderLayout());
                scrollPanel.add(profilePanel, BorderLayout.NORTH);
                JScrollPane scroll = new JScrollPane(scrollPanel);

                contentPanel.add(headerPanel, BorderLayout.NORTH);
                contentPanel.add(scroll, BorderLayout.CENTER);
            } else {
                JLabel errorLabel = new JLabel("Profile not found");
                errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(errorLabel, BorderLayout.CENTER);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading profile: " + e.getMessage());
            errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            contentPanel.add(errorLabel, BorderLayout.CENTER);
        }

        refresh();
    }

    private void addProfileField(JPanel panel, String label, String value) {
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("Arial", Font.BOLD, 12));
        labelField.setForeground(new Color(52, 73, 94));

        JLabel valueField = new JLabel(value);
        valueField.setFont(new Font("Arial", Font.PLAIN, 12));
        valueField.setForeground(new Color(44, 62, 80));

        panel.add(labelField);
        panel.add(valueField);
    }

    private void styleDataTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.setSelectionBackground(new Color(149, 165, 166));
        table.setGridColor(new Color(189, 195, 199));
    }

    private void showDepartment() {
        contentPanel.removeAll();
        contentPanel.setBorder(null);
        contentPanel.setLayout(new BorderLayout());

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {

            String query = "SELECT d.d_name, d.d_head, j.designation, j.work_hours " +
                    "FROM Employee e " +
                    "LEFT JOIN Department d ON e.department_id = d.department_id " +
                    "LEFT JOIN Job_Role j ON e.role_id = j.role_id " +
                    "WHERE e.EmpID = ?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JPanel headerPanel = new JPanel();
                headerPanel.setBackground(new Color(155, 89, 182));
                JLabel headerLabel = new JLabel("Department & Role Information");
                headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
                headerLabel.setForeground(Color.WHITE);
                headerPanel.add(headerLabel);

                JPanel infoPanel = new JPanel(new GridLayout(0, 2, 15, 15));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                infoPanel.setBackground(Color.WHITE);

                addProfileField(infoPanel, "Department:", rs.getString("d_name") != null ? rs.getString("d_name") : "N/A");
                addProfileField(infoPanel, "Department Head:", rs.getString("d_head") != null ? rs.getString("d_head") : "N/A");
                addProfileField(infoPanel, "Designation:", rs.getString("designation") != null ? rs.getString("designation") : "N/A");
                addProfileField(infoPanel, "Work Hours:", rs.getInt("work_hours") > 0 ? String.valueOf(rs.getInt("work_hours")) : "N/A");

                JPanel scrollPanel = new JPanel(new BorderLayout());
                scrollPanel.add(infoPanel, BorderLayout.NORTH);
                JScrollPane scroll = new JScrollPane(scrollPanel);

                contentPanel.add(headerPanel, BorderLayout.NORTH);
                contentPanel.add(scroll, BorderLayout.CENTER);
            } else {
                JPanel headerPanel = new JPanel();
                headerPanel.setBackground(new Color(155, 89, 182));
                JLabel headerLabel = new JLabel("Department & Role Information");
                headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
                headerLabel.setForeground(Color.WHITE);
                headerPanel.add(headerLabel);

                JLabel errorLabel = new JLabel("Department and role details not found");
                errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                contentPanel.add(headerPanel, BorderLayout.NORTH);
                contentPanel.add(errorLabel, BorderLayout.CENTER);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(155, 89, 182));
            JLabel headerLabel = new JLabel("Department & Role Information");
            headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);

            JLabel errorLabel = new JLabel("Error loading department details: " + e.getMessage());
            errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            contentPanel.add(headerPanel, BorderLayout.NORTH);
            contentPanel.add(errorLabel, BorderLayout.CENTER);
        }

        refresh();
    }

    private void showAttendance() {
        contentPanel.removeAll();
        contentPanel.setBorder(null);
        contentPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        JLabel headerLabel = new JLabel("Attendance Log");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "In", "Out", "Shift", "Remark"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleDataTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        int empId = getEmployeeId();
        boolean hasMarkedInToday = false;
        boolean hasMarkedOutToday = false;

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT work_date, in_time, out_time, shift, remark " +
                    "FROM attendance_log WHERE EmpID = ? ORDER BY work_date DESC, in_time DESC"
            );
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getDate("work_date"),
                        rs.getTime("in_time"),
                        rs.getTime("out_time"),
                        rs.getString("shift"),
                        rs.getString("remark")
                });
            }

            PreparedStatement todayPs = con.prepareStatement(
                    "SELECT in_time, out_time FROM attendance_log WHERE EmpID = ? AND work_date = CURDATE() ORDER BY att_id DESC LIMIT 1"
            );
            todayPs.setInt(1, empId);
            ResultSet todayRs = todayPs.executeQuery();
            if (todayRs.next()) {
                hasMarkedInToday = todayRs.getTime("in_time") != null;
                hasMarkedOutToday = todayRs.getTime("out_time") != null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading attendance: " + e.getMessage());
        }

        JButton markBtn = new JButton();
        if (!hasMarkedInToday) {
            markBtn.setText("Mark In");
        } else if (!hasMarkedOutToday) {
            markBtn.setText("Mark Out");
        } else {
            markBtn.setText("Attendance Completed");
            markBtn.setEnabled(false);
        }

        markBtn.addActionListener(e -> markAttendance());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(markBtn);

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void markAttendance() {
        int empId = getEmployeeId();

        if (empId == -1) {
            JOptionPane.showMessageDialog(frame, "Invalid employee account mapping");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement checkPs = con.prepareStatement(
                    "SELECT att_id, in_time, out_time FROM attendance_log WHERE EmpID = ? AND work_date = CURDATE() ORDER BY att_id DESC LIMIT 1"
            );
            checkPs.setInt(1, empId);
            ResultSet checkRs = checkPs.executeQuery();

            if (!checkRs.next()) {
                int nextAttId = getNextAttendanceId(con);

                PreparedStatement inPs = con.prepareStatement(
                        "INSERT INTO attendance_log (att_id, work_date, in_time, out_time, shift, remark, EmpID) " +
                        "VALUES (?, CURDATE(), CURTIME(), NULL, ?, ?, ?)"
                );
                inPs.setInt(1, nextAttId);
                inPs.setString(2, "Day");
                inPs.setString(3, "Present");
                inPs.setInt(4, empId);

                int rows = inPs.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Marked IN successfully");
                } else {
                    JOptionPane.showMessageDialog(frame, "Could not mark IN");
                }
                showAttendance();
                return;
            }

            int attId = checkRs.getInt("att_id");
            Time outTime = checkRs.getTime("out_time");

            if (outTime == null) {
                PreparedStatement outPs = con.prepareStatement(
                        "UPDATE attendance_log SET out_time = CURTIME() WHERE att_id = ?"
                );
                outPs.setInt(1, attId);
                int rows = outPs.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Marked OUT successfully");
                } else {
                    JOptionPane.showMessageDialog(frame, "Could not mark OUT");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "You already marked IN and OUT for today");
            }

            showAttendance();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error marking attendance");
        }
    }

    private void applyLeave() {
        String start = JOptionPane.showInputDialog("Start Date (YYYY-MM-DD)");
        String end = JOptionPane.showInputDialog("End Date (YYYY-MM-DD)");

        if (start == null || end == null || start.trim().isEmpty() || end.trim().isEmpty()) {
            return;
        }

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            // Get next leave request ID
            String maxIdQuery = "SELECT COALESCE(MAX(leave_id), 0) + 1 AS next_id FROM Leave_Request";
            PreparedStatement getIdPs = con.prepareStatement(maxIdQuery);
            ResultSet idRs = getIdPs.executeQuery();
            int nextId = 1;
            if (idRs.next()) {
                nextId = idRs.getInt("next_id");
            }

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Leave_Request (leave_id, start_date, end_date, status, EmpID) VALUES (?, ?, ?, 'Pending', ?)"
            );
            ps.setInt(1, nextId);
            ps.setString(2, start);
            ps.setString(3, end);
            ps.setInt(4, empId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Leave Applied");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error applying leave: " + e.getMessage());
        }
    }

    private void showLeaveStatus() {
        contentPanel.removeAll();
        contentPanel.setBorder(null);
        contentPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(230, 126, 34));
        JLabel headerLabel = new JLabel("Leave Request Status");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Start Date", "End Date", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleDataTable(table);

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT start_date, end_date, status FROM Leave_Request WHERE EmpID = ? ORDER BY start_date DESC"
            );
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading leave status: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        refresh();
    }

    private void showPayroll() {
        contentPanel.removeAll();
        contentPanel.setBorder(null);
        contentPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113));
        JLabel headerLabel = new JLabel("Payroll Information");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Payroll ID", "Pay Date", "Total Amount", "Transaction ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleDataTable(table);

        int empId = getEmployeeId();
        double totalAmount = 0;

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT payroll_id, paydate, total_amount, transaction_id " +
                    "FROM Payroll WHERE EmpID = ? ORDER BY paydate DESC"
            );
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("payroll_id"),
                        rs.getDate("paydate"),
                        String.format("₹ %.2f", rs.getDouble("total_amount")),
                        rs.getString("transaction_id")
                });
                totalAmount += rs.getDouble("total_amount");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading payroll: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(236, 240, 241));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel summaryLabel = new JLabel("Total Payroll Amount: " + String.format("₹ %.2f", totalAmount));
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 13));
        summaryLabel.setForeground(new Color(44, 62, 80));
        summaryPanel.add(summaryLabel);

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void showProjects() {
        contentPanel.removeAll();
        contentPanel.setBorder(null);
        contentPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(26, 188, 156));
        JLabel headerLabel = new JLabel("Assigned Projects");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Project Name", "Start Date", "End Date", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleDataTable(table);

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT p.* FROM projects p " +
                    "JOIN employee_projects ep ON p.project_id = ep.project_id " +
                    "WHERE ep.EmpId = ? ORDER BY p.StartDate DESC";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Pname"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("Status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading projects: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        refresh();
    }

    private void showMeetings() {
        contentPanel.removeAll();
        contentPanel.setBorder(null);
        contentPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(231, 76, 60));
        JLabel headerLabel = new JLabel("Department Meetings");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "Time", "Topic"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleDataTable(table);

        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT m_date, m_time, topic FROM meeting ORDER BY m_date DESC, m_time DESC");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getDate("m_date"),
                        rs.getTime("m_time"),
                        rs.getString("topic")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading meetings: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        refresh();
    }

    private void changePassword() {
        String newPass = JOptionPane.showInputDialog("New Password");

        if (newPass == null || newPass.trim().isEmpty()) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET password = ? WHERE username = ?"
            );
            ps.setString(1, newPass);
            ps.setString(2, currentUser.getUsername());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Password Updated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        frame.dispose();
        new LoginUI();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}