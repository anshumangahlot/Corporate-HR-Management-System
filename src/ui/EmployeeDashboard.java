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
        frame.setSize(1100, 700);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome, " + user.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        JPanel side = new JPanel(new GridLayout(6, 2, 5, 5));

        JButton profileBtn = new JButton("Profile");
        JButton deptBtn = new JButton("Department & Role");
        JButton attendBtn = new JButton("Attendance");
        JButton markBtn = new JButton("Mark Attendance");
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
        side.add(markBtn);
        side.add(leaveBtn);
        side.add(leaveStatusBtn);
        side.add(payrollBtn);
        side.add(projectBtn);
        side.add(meetingBtn);
        side.add(passBtn);
        side.add(logoutBtn);

        frame.add(side, BorderLayout.WEST);

        contentPanel = new JPanel();
        frame.add(contentPanel, BorderLayout.CENTER);

        profileBtn.addActionListener(e -> showProfile());
        deptBtn.addActionListener(e -> showDepartment());
        attendBtn.addActionListener(e -> showAttendance());
        markBtn.addActionListener(e -> markAttendance());
        leaveBtn.addActionListener(e -> applyLeave());
        leaveStatusBtn.addActionListener(e -> showLeaveStatus());
        payrollBtn.addActionListener(e -> showPayroll());
        projectBtn.addActionListener(e -> showProjects());
        meetingBtn.addActionListener(e -> showMeetings());
        passBtn.addActionListener(e -> changePassword());
        logoutBtn.addActionListener(e -> logout());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private int getEmployeeId() {
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
        contentPanel.setLayout(new GridLayout(6, 1));

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT EmpID, Emp_name, DOB, Gender, Email, Street FROM Employee WHERE EmpID = ?"
            );
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                contentPanel.add(new JLabel("Name: " + rs.getString("Emp_name")));
                contentPanel.add(new JLabel("DOB: " + rs.getDate("DOB")));
                contentPanel.add(new JLabel("Gender: " + rs.getString("Gender")));
                contentPanel.add(new JLabel("Email: " + rs.getString("Email")));
                contentPanel.add(new JLabel("Street: " + rs.getString("Street")));
                
                // Get phone from Employee_Phones
                PreparedStatement phonePs = con.prepareStatement("SELECT Phone_Number FROM Employee_Phones WHERE EmpID = ? LIMIT 1");
                phonePs.setInt(1, empId);
                ResultSet phoneRs = phonePs.executeQuery();
                if (phoneRs.next()) {
                    contentPanel.add(new JLabel("Phone: " + phoneRs.getString("Phone_Number")));
                } else {
                    contentPanel.add(new JLabel("Phone: Not available"));
                }
            } else {
                contentPanel.add(new JLabel("Profile not found"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentPanel.add(new JLabel("Error loading profile"));
        }

        refresh();
    }

    private void showDepartment() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(4, 1));

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
                contentPanel.add(new JLabel("Department: " + rs.getString("d_name")));
                contentPanel.add(new JLabel("Head: " + rs.getString("d_head")));
                contentPanel.add(new JLabel("Role: " + rs.getString("designation")));
                contentPanel.add(new JLabel("Work Hours: " + (rs.getInt("work_hours") > 0 ? rs.getInt("work_hours") : "N/A")));
            } else {
                contentPanel.add(new JLabel("Department details not found"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentPanel.add(new JLabel("Error loading department details"));
        }

        refresh();
    }

    private void showAttendance() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "In", "Out", "Shift", "Remark"}, 0
        );

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        int empId = getEmployeeId();

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

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading attendance");
        }

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        refresh();
    }

    private void markAttendance() {
        int empId = getEmployeeId();

        if (empId == -1) {
            JOptionPane.showMessageDialog(frame, "Invalid employee username");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement checkPs = con.prepareStatement(
                    "SELECT * FROM attendance_log WHERE EmpID = ? AND work_date = CURDATE()"
            );
            checkPs.setInt(1, empId);
            ResultSet checkRs = checkPs.executeQuery();

            if (checkRs.next()) {
                JOptionPane.showMessageDialog(frame, "Attendance already marked for today");
                showAttendance();
                return;
            }

            int nextAttId = getNextAttendanceId(con);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO attendance_log (att_id, work_date, in_time, out_time, shift, remark, EmpID) " +
                    "VALUES (?, CURDATE(), CURTIME(), NULL, ?, ?, ?)"
            );
            ps.setInt(1, nextAttId);
            ps.setString(2, "Day");
            ps.setString(3, "Present");
            ps.setInt(4, empId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Attendance Marked Successfully");
                showAttendance();
            } else {
                JOptionPane.showMessageDialog(frame, "Attendance not marked");
            }

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
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Start", "End", "Status"}, 0
        );

        JTable table = new JTable(model);

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT start_date, end_date, status FROM Leave_Request WHERE EmpID = ?"
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
        }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    private void showPayroll() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Payroll ID", "PayDate", "Total Amount", "Transaction ID"}, 0
        );

        JTable table = new JTable(model);

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT payroll_id, paydate, total_amount, transaction_id " +
                    "FROM Payroll WHERE EmpID = ?"
            );
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("payroll_id"),
                        rs.getDate("paydate"),
                        rs.getDouble("total_amount"),
                        rs.getString("transaction_id")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading payroll");
        }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    private void showProjects() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Project", "Start", "End", "Status"}, 0
        );

        JTable table = new JTable(model);

        int empId = getEmployeeId();

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT p.* FROM projects p " +
                    "JOIN employee_projects ep ON p.project_id = ep.project_id " +
                    "WHERE ep.EmpId = ?";

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
        }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    private void showMeetings() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "Time", "Topic"}, 0
        );

        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM meeting");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getDate("Date"),
                        rs.getString("Timing"),
                        rs.getString("Topic")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPanel.add(new JScrollPane(table));
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