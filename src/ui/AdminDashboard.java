package ui;

import db.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboard {

    private JFrame frame;
    private JPanel contentPanel;
    private User currentUser;

    public AdminDashboard(User user) {
        this.currentUser = user;

        frame = new JFrame("Admin Dashboard");
        frame.setSize(1200, 750);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Admin Dashboard - Welcome, " + user.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        JPanel side = new JPanel(new GridLayout(11, 1, 5, 5));

        JButton summaryBtn = new JButton("Summary");
        JButton employeesBtn = new JButton("Employees");
        JButton addEmpBtn = new JButton("Add Employee");
        JButton removeEmpBtn = new JButton("Remove Employee");
        JButton departmentsBtn = new JButton("Departments");
        JButton leaveBtn = new JButton("Leave Requests");
        JButton attendanceBtn = new JButton("Attendance Logs");
        JButton payrollBtn = new JButton("Payroll");
        JButton projectsBtn = new JButton("Projects");
        JButton meetingsBtn = new JButton("Meetings");
        JButton passBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        side.add(summaryBtn);
        side.add(employeesBtn);
        side.add(addEmpBtn);
        side.add(removeEmpBtn);
        side.add(departmentsBtn);
        side.add(leaveBtn);
        side.add(attendanceBtn);
        side.add(payrollBtn);
        side.add(projectsBtn);
        side.add(meetingsBtn);
        side.add(passBtn);
        side.add(logoutBtn);

        frame.add(side, BorderLayout.WEST);

        contentPanel = new JPanel();
        frame.add(contentPanel, BorderLayout.CENTER);

        summaryBtn.addActionListener(e -> showSummary());
        employeesBtn.addActionListener(e -> showEmployees());
        addEmpBtn.addActionListener(e -> addEmployee());
        removeEmpBtn.addActionListener(e -> removeEmployee());
        departmentsBtn.addActionListener(e -> showDepartments());
        leaveBtn.addActionListener(e -> showLeaveRequests());
        attendanceBtn.addActionListener(e -> showAttendance());
        payrollBtn.addActionListener(e -> showPayroll());
        projectsBtn.addActionListener(e -> showProjects());
        meetingsBtn.addActionListener(e -> showMeetings());
        passBtn.addActionListener(e -> changePassword());
        logoutBtn.addActionListener(e -> logout());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showSummary();
    }

    private void showSummary() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(2, 2, 20, 20));

        try (Connection con = DBConnection.getConnection()) {
            contentPanel.add(createCard("Total Employees", String.valueOf(getCount(con, "SELECT COUNT(*) FROM Employee"))));
            contentPanel.add(createCard("Pending Leave Requests", String.valueOf(getCount(con, "SELECT COUNT(*) FROM Leave_Request WHERE status = 'Pending'"))));
            contentPanel.add(createCard("Active Projects", String.valueOf(getCount(con, "SELECT COUNT(*) FROM Projects WHERE Status <> 'Completed'"))));
            contentPanel.add(createCard("Upcoming Meetings", String.valueOf(getCount(con, "SELECT COUNT(*) FROM Meeting WHERE m_date >= CURDATE()"))));
        } catch (Exception e) {
            e.printStackTrace();
            contentPanel.add(new JLabel("Error loading summary", SwingConstants.CENTER));
        }

        refresh();
    }

    private JPanel createCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel titleLabel = new JLabel(label, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 32));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private int getCount(Connection con, String sql) {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showEmployees() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Email", "Department", "Role"}, 0
        );
        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT e.EmpID, e.Emp_name, e.Email, d.d_name, j.designation " +
                    "FROM Employee e " +
                    "LEFT JOIN Department d ON e.department_id = d.department_id " +
                    "LEFT JOIN Job_Role j ON e.role_id = j.role_id";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("EmpID"),
                        rs.getString("Emp_name"),
                        rs.getString("Email"),
                        rs.getString("d_name"),
                        rs.getString("designation")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading employees");
        }

        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private void showDepartments() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Head", "Employees"}, 0
        );
        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT d.department_id, d.d_name, d.d_head, COUNT(e.EmpID) AS employees " +
                    "FROM Department d " +
                    "LEFT JOIN Employee e ON d.department_id = e.department_id " +
                    "GROUP BY d.department_id, d.d_name, d.d_head";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("department_id"),
                        rs.getString("d_name"),
                        rs.getString("d_head"),
                        rs.getInt("employees")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading departments");
        }

        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private void showLeaveRequests() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Leave ID", "Employee", "Start", "End", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT lr.leave_id, e.Emp_name, lr.start_date, lr.end_date, lr.status " +
                    "FROM Leave_Request lr " +
                    "LEFT JOIN Employee e ON lr.EmpID = e.EmpID";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("leave_id"),
                        rs.getString("Emp_name"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading leave requests");
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton approveBtn = new JButton("Approve Selected");
        JButton rejectBtn = new JButton("Reject Selected");

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a leave request");
                return;
            }
            int leaveId = (int) model.getValueAt(row, 0);
            updateLeaveStatus(leaveId, "Approved");
            showLeaveRequests();
        });

        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a leave request");
                return;
            }
            int leaveId = (int) model.getValueAt(row, 0);
            updateLeaveStatus(leaveId, "Rejected");
            showLeaveRequests();
        });

        bottomPanel.add(approveBtn);
        bottomPanel.add(rejectBtn);

        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void updateLeaveStatus(int leaveId, String status) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Leave_Request SET status = ? WHERE leave_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, leaveId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Leave request " + status.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating leave request");
        }
    }

    private void showAttendance() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Date", "In", "Out", "Shift", "Remark", "Employee"}, 0
        );
        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT a.att_id, a.work_date, a.in_time, a.out_time, a.shift, a.remark, e.Emp_name " +
                    "FROM Attendance_Log a " +
                    "LEFT JOIN Employee e ON a.EmpID = e.EmpID " +
                    "ORDER BY a.work_date DESC, a.in_time DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("att_id"),
                        rs.getDate("work_date"),
                        rs.getTime("in_time"),
                        rs.getTime("out_time"),
                        rs.getString("shift"),
                        rs.getString("remark"),
                        rs.getString("Emp_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading attendance logs");
        }

        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private void showPayroll() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Payroll ID", "Date", "Amount", "Transaction", "Employee"}, 0
        );
        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT p.payroll_id, p.paydate, p.total_amount, p.transaction_id, e.Emp_name " +
                    "FROM Payroll p " +
                    "LEFT JOIN Employee e ON p.EmpID = e.EmpID";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("payroll_id"),
                        rs.getDate("paydate"),
                        rs.getDouble("total_amount"),
                        rs.getString("transaction_id"),
                        rs.getString("Emp_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading payroll records");
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton generateBtn = new JButton("Generate Payroll");

        generateBtn.addActionListener(e -> generatePayroll());

        bottomPanel.add(generateBtn);

        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void generatePayroll() {
        String payDate = JOptionPane.showInputDialog(frame, "Payroll Date (YYYY-MM-DD):");
        if (payDate == null || payDate.trim().isEmpty()) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            // Get next payroll ID
            String maxIdQuery = "SELECT COALESCE(MAX(payroll_id), 0) + 1 AS next_id FROM Payroll";
            PreparedStatement getIdPs = con.prepareStatement(maxIdQuery);
            ResultSet idRs = getIdPs.executeQuery();
            int nextId = 1;
            if (idRs.next()) {
                nextId = idRs.getInt("next_id");
            }

            // Get all employees
            String empQuery = "SELECT EmpID FROM Employee";
            PreparedStatement empPs = con.prepareStatement(empQuery);
            ResultSet empRs = empPs.executeQuery();

            int inserted = 0;
            while (empRs.next()) {
                int empId = empRs.getInt("EmpID");
                double salary = 50000; // Default salary
                String transId = "TXN-" + System.currentTimeMillis();

                String insertQuery = "INSERT INTO Payroll (payroll_id, paydate, total_amount, transaction_id, EmpID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertPs = con.prepareStatement(insertQuery);
                insertPs.setInt(1, nextId++);
                insertPs.setString(2, payDate);
                insertPs.setDouble(3, salary);
                insertPs.setString(4, transId);
                insertPs.setInt(5, empId);
                insertPs.executeUpdate();
                inserted++;
            }

            JOptionPane.showMessageDialog(frame, "Payroll generated for " + inserted + " employees");
            showPayroll();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error generating payroll: " + e.getMessage());
        }
    }

    private void showProjects() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Project ID", "Name", "Start", "End", "Status", "Team Lead"}, 0
        );
        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT p.project_id, p.PName, p.StartDate, p.EndDate, p.Status, e.Emp_name " +
                    "FROM Projects p " +
                    "LEFT JOIN Employee e ON p.TeamLead = e.EmpID";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("project_id"),
                        rs.getString("PName"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("Status"),
                        rs.getString("Emp_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading projects");
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Project");

        addBtn.addActionListener(e -> addProject());

        bottomPanel.add(addBtn);

        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void addProject() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField startField = new JTextField();
        JTextField endField = new JTextField();
        JTextField statusField = new JTextField("Active");
        JTextField leadField = new JTextField();

        panel.add(new JLabel("Project Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Team Lead ID (optional):"));
        panel.add(leadField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Project", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String maxIdQuery = "SELECT COALESCE(MAX(project_id), 0) + 1 AS next_id FROM Projects";
            PreparedStatement getIdPs = con.prepareStatement(maxIdQuery);
            ResultSet idRs = getIdPs.executeQuery();
            int nextId = 1;
            if (idRs.next()) {
                nextId = idRs.getInt("next_id");
            }

            // Validate Team Lead ID if provided
            Integer teamLeadId = null;
            if (!leadField.getText().trim().isEmpty()) {
                teamLeadId = Integer.parseInt(leadField.getText());
                String checkQuery = "SELECT EmpID FROM Employee WHERE EmpID = ?";
                PreparedStatement checkPs = con.prepareStatement(checkQuery);
                checkPs.setInt(1, teamLeadId);
                ResultSet checkRs = checkPs.executeQuery();
                if (!checkRs.next()) {
                    JOptionPane.showMessageDialog(frame, "Team Lead ID does not exist in Employee table");
                    return;
                }
            }

            String insertQuery = "INSERT INTO Projects (project_id, PName, StartDate, EndDate, Status, TeamLead) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setInt(1, nextId);
            ps.setString(2, nameField.getText());
            ps.setString(3, startField.getText());
            ps.setString(4, endField.getText());
            ps.setString(5, statusField.getText());
            if (teamLeadId != null) {
                ps.setInt(6, teamLeadId);
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Project added successfully");
            showProjects();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding project: " + e.getMessage());
        }
    }

    private void showMeetings() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "Time", "Topic", "Department"}, 0
        );
        JTable table = new JTable(model);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT m.m_date, m.m_time, m.topic, d.d_name " +
                    "FROM Meeting m " +
                    "LEFT JOIN Department d ON m.dept_id = d.department_id " +
                    "ORDER BY m.m_date DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getDate("m_date"),
                        rs.getTime("m_time"),
                        rs.getString("topic"),
                        rs.getString("d_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading meetings");
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Meeting");

        addBtn.addActionListener(e -> addMeeting());

        bottomPanel.add(addBtn);

        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void addMeeting() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField topicField = new JTextField();
        JTextField deptField = new JTextField();

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM:SS):"));
        panel.add(timeField);
        panel.add(new JLabel("Topic:"));
        panel.add(topicField);
        panel.add(new JLabel("Department ID:"));
        panel.add(deptField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Meeting", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String maxIdQuery = "SELECT COALESCE(MAX(meeting_id), 0) + 1 AS next_id FROM Meeting";
            PreparedStatement getIdPs = con.prepareStatement(maxIdQuery);
            ResultSet idRs = getIdPs.executeQuery();
            int nextId = 1;
            if (idRs.next()) {
                nextId = idRs.getInt("next_id");
            }

            String insertQuery = "INSERT INTO Meeting (meeting_id, m_date, m_time, topic, dept_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setInt(1, nextId);
            ps.setString(2, dateField.getText());
            ps.setString(3, timeField.getText());
            ps.setString(4, topicField.getText());
            ps.setInt(5, Integer.parseInt(deptField.getText()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Meeting added successfully");
            showMeetings();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding meeting: " + e.getMessage());
        }
    }

    private void changePassword() {
        String newPass = JOptionPane.showInputDialog(frame, "New Password");

        if (newPass == null || newPass.trim().isEmpty()) {
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE users SET password = ? WHERE username = ?")) {
            ps.setString(1, newPass);
            ps.setString(2, currentUser.getUsername());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Password Updated");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating password");
        }
    }

    private void addEmployee() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField dobField = new JTextField("YYYY-MM-DD");
        JTextField streetField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField roleField = new JTextField();

        panel.add(new JLabel("Employee ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("DOB (YYYY-MM-DD):"));
        panel.add(dobField);
        panel.add(new JLabel("Street:"));
        panel.add(streetField);
        panel.add(new JLabel("Department ID:"));
        panel.add(deptField);
        panel.add(new JLabel("Role ID:"));
        panel.add(roleField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String insertQuery = "INSERT INTO Employee (EmpID, Emp_name, Gender, DOB, Email, Street, department_id, role_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, nameField.getText());
            ps.setString(3, genderField.getText());
            ps.setString(4, dobField.getText());
            ps.setString(5, emailField.getText());
            ps.setString(6, streetField.getText());
            
            if (!deptField.getText().trim().isEmpty()) {
                ps.setInt(7, Integer.parseInt(deptField.getText()));
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            
            if (!roleField.getText().trim().isEmpty()) {
                ps.setInt(8, Integer.parseInt(roleField.getText()));
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Employee added successfully");
            showEmployees();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding employee: " + e.getMessage());
        }
    }

    private void removeEmployee() {
        String empId = JOptionPane.showInputDialog(frame, "Enter Employee ID to remove:");
        if (empId == null || empId.trim().isEmpty()) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to remove employee " + empId + "?", 
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            // Delete related records first
            String deleteProjectsQuery = "DELETE FROM Employee_Projects WHERE EmpID = ?";
            PreparedStatement deleteProjectsPs = con.prepareStatement(deleteProjectsQuery);
            deleteProjectsPs.setInt(1, Integer.parseInt(empId));
            deleteProjectsPs.executeUpdate();

            String deleteAttendanceQuery = "DELETE FROM Attendance_Log WHERE EmpID = ?";
            PreparedStatement deleteAttendancePs = con.prepareStatement(deleteAttendanceQuery);
            deleteAttendancePs.setInt(1, Integer.parseInt(empId));
            deleteAttendancePs.executeUpdate();

            String deleteLeaveQuery = "DELETE FROM Leave_Request WHERE EmpID = ?";
            PreparedStatement deleteLeavePs = con.prepareStatement(deleteLeaveQuery);
            deleteLeavePs.setInt(1, Integer.parseInt(empId));
            deleteLeavePs.executeUpdate();

            String deletePayrollQuery = "DELETE FROM Payroll WHERE EmpID = ?";
            PreparedStatement deletePayrollPs = con.prepareStatement(deletePayrollQuery);
            deletePayrollPs.setInt(1, Integer.parseInt(empId));
            deletePayrollPs.executeUpdate();

            String deletePhonesQuery = "DELETE FROM Employee_Phones WHERE EmpID = ?";
            PreparedStatement deletePhonesPs = con.prepareStatement(deletePhonesQuery);
            deletePhonesPs.setInt(1, Integer.parseInt(empId));
            deletePhonesPs.executeUpdate();

            // Delete the employee
            String deleteEmpQuery = "DELETE FROM Employee WHERE EmpID = ?";
            PreparedStatement deleteEmpPs = con.prepareStatement(deleteEmpQuery);
            deleteEmpPs.setInt(1, Integer.parseInt(empId));
            int rows = deleteEmpPs.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Employee removed successfully");
                showEmployees();
            } else {
                JOptionPane.showMessageDialog(frame, "Employee not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing employee: " + e.getMessage());
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

