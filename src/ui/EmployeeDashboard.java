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
        frame.setSize(900, 550);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome, " + user.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        // Sidebar
        JPanel side = new JPanel(new GridLayout(10, 1, 5, 5));

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

        // Actions
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

    // 🔹 PROFILE
    private void showProfile() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(6,1));

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM employee WHERE email=?");
            ps.setString(1, currentUser.getUsername());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                contentPanel.add(new JLabel("Name: " + rs.getString("emp_name")));
                contentPanel.add(new JLabel("DOB: " + rs.getDate("dob")));
                contentPanel.add(new JLabel("Gender: " + rs.getString("gender")));
                contentPanel.add(new JLabel("Email: " + rs.getString("email")));
                contentPanel.add(new JLabel("Phone: " + rs.getString("phone")));
                contentPanel.add(new JLabel("Address: " + rs.getString("address")));
            }

        } catch (Exception e) { e.printStackTrace(); }

        refresh();
    }

    // 🔹 DEPARTMENT + ROLE
    private void showDepartment() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(4,1));

        try (Connection con = DBConnection.getConnection()) {

            String query = "SELECT d.d_name, d.d_location, j.designation, j.work_hours " +
                    "FROM employee e " +
                    "JOIN department d ON e.department_id=d.department_id " +
                    "JOIN job_role j ON e.role_id=j.role_id " +
                    "WHERE e.email=?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, currentUser.getUsername());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                contentPanel.add(new JLabel("Department: " + rs.getString("d_name")));
                contentPanel.add(new JLabel("Location: " + rs.getString("d_location")));
                contentPanel.add(new JLabel("Role: " + rs.getString("designation")));
                contentPanel.add(new JLabel("Work Hours: " + rs.getString("work_hours")));
            }

        } catch (Exception e) { e.printStackTrace(); }

        refresh();
    }

    // 🔹 ATTENDANCE VIEW
    private void showAttendance() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date","In","Out","Shift","Remark"},0);

        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {

            String query = "SELECT * FROM attendance_log a JOIN employee e ON a.EmpId=e.EmpId WHERE e.email=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, currentUser.getUsername());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getDate("attendance_id"),
                        rs.getTime("in_time"),
                        rs.getTime("out_time"),
                        rs.getString("shift"),
                        rs.getString("remark")
                });
            }

        } catch (Exception e) { e.printStackTrace(); }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    // 🔹 MARK ATTENDANCE
    private void markAttendance() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps1 = con.prepareStatement(
                    "SELECT EmpId FROM employee WHERE email=?");
            ps1.setString(1, currentUser.getUsername());
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("EmpId");

                PreparedStatement ps2 = con.prepareStatement(
                        "INSERT INTO attendance_log (EmpId,in_time,shift,remark) VALUES (?,NOW(),'Day','Present')");
                ps2.setInt(1, id);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(frame,"Attendance Marked");
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    // 🔹 APPLY LEAVE
    private void applyLeave() {
        String start = JOptionPane.showInputDialog("Start Date (YYYY-MM-DD)");
        String end = JOptionPane.showInputDialog("End Date");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps1 = con.prepareStatement(
                    "SELECT EmpId FROM employee WHERE email=?");
            ps1.setString(1, currentUser.getUsername());
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("EmpId");

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO leave_request (start_date,end_date,approval_status,EmpId) VALUES (?,?, 'Pending',?)");
                ps.setString(1,start);
                ps.setString(2,end);
                ps.setInt(3,id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame,"Leave Applied");
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    // 🔹 LEAVE STATUS
    private void showLeaveStatus() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Start","End","Status"},0);

        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {

            String query = "SELECT * FROM leave_request l JOIN employee e ON l.EmpId=e.EmpId WHERE e.email=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,currentUser.getUsername());

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("approval_status")
                });
            }

        } catch(Exception e){ e.printStackTrace(); }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    // 🔹 PAYROLL
    private void showPayroll() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"PayDate","Salary","Bonus","HRA","Tax"},0);

        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {

            String query = "SELECT * FROM payroll_record p JOIN employee e ON p.EmpId=e.EmpId WHERE e.email=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,currentUser.getUsername());

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getDate("paydate"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("bonus"),
                        rs.getDouble("house_rent_allowance"),
                        rs.getDouble("tax_deduction")
                });
            }

        } catch(Exception e){ e.printStackTrace(); }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    // 🔹 PROJECTS
    private void showProjects() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Project","Start","End","Status"},0);

        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {

            String query = "SELECT p.* FROM projects p JOIN employee_projects ep ON p.project_id=ep.project_id " +
                    "JOIN employee e ON ep.EmpId=e.EmpId WHERE e.email=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,currentUser.getUsername());

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("Pname"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("Status")
                });
            }

        } catch(Exception e){ e.printStackTrace(); }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    // 🔹 MEETINGS
    private void showMeetings() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date","Time","Topic"},0);

        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM meeting");

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getDate("Date"),
                        rs.getString("Timing"),
                        rs.getString("Topic")
                });
            }

        } catch(Exception e){ e.printStackTrace(); }

        contentPanel.add(new JScrollPane(table));
        refresh();
    }

    // 🔹 PASSWORD
    private void changePassword() {
        String newPass = JOptionPane.showInputDialog("New Password");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET password=? WHERE username=?");
            ps.setString(1,newPass);
            ps.setString(2,currentUser.getUsername());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame,"Password Updated");

        } catch(Exception e){ e.printStackTrace(); }
    }

    // 🔹 LOGOUT
    private void logout() {
        frame.dispose();
        new LoginUI();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}