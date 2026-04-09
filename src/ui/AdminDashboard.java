package ui;

import db.DBConnection;
import exceptions.PhoneNumberValidationException;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.User;

/**
 * AdminDashboard Class
 * Demonstrates inheritance from abstract Dashboard class (CO2)
 * Shows polymorphism through different implementation than EmployeeDashboard
 */
public class AdminDashboard extends Dashboard {

    private JPanel contentPanel;

    public AdminDashboard(User user) {
        super(user);
        initializeDashboard();
    }

    @Override
    public void initializeDashboard() {

        frame = new JFrame("Admin Dashboard");
        frame.setSize(1200, 750);
        frame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("HR Management System", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Admin Portal - Welcome, " + currentUser.getUsername(), SwingConstants.RIGHT);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 235, 247));

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(subtitle, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        JPanel side = new JPanel(new GridLayout(11, 1, 5, 5));
        side.setBackground(new Color(44, 62, 80));
        side.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton summaryBtn = new JButton("Summary");
        JButton employeesBtn = new JButton("Employees");
        JButton departmentsBtn = new JButton("Departments");
        JButton rolesBtn = new JButton("Roles");
        JButton leaveBtn = new JButton("Leave Requests");
        JButton attendanceBtn = new JButton("Attendance Logs");
        JButton payrollBtn = new JButton("Payroll");
        JButton projectsBtn = new JButton("Projects");
        JButton meetingsBtn = new JButton("Meetings");
        JButton passBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        side.add(summaryBtn);
        side.add(employeesBtn);
        side.add(departmentsBtn);
        side.add(rolesBtn);
        side.add(leaveBtn);
        side.add(attendanceBtn);
        side.add(payrollBtn);
        side.add(projectsBtn);
        side.add(meetingsBtn);
        side.add(passBtn);
        side.add(logoutBtn);

        JButton[] navButtons = {
            summaryBtn, employeesBtn, departmentsBtn, rolesBtn, leaveBtn,
            attendanceBtn, payrollBtn, projectsBtn, meetingsBtn, passBtn
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

        summaryBtn.addActionListener(e -> showSummary());
        employeesBtn.addActionListener(e -> showEmployees());
        departmentsBtn.addActionListener(e -> showDepartments());
        rolesBtn.addActionListener(e -> showRoles());
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
        contentPanel.setBackground(new Color(236, 240, 241));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(label, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        valueLabel.setForeground(new Color(41, 128, 185));

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
                new String[]{"ID", "Name", "Email", "Phone", "Department", "Role"}, 0
        );
        JTable table = new JTable(model);
        styleTable(table);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT e.EmpID, e.Emp_name, e.Email, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT ep.Phone_Number SEPARATOR ', '), 'Not available') AS phones, " +
                    "d.d_name, j.designation " +
                    "FROM Employee e " +
                    "LEFT JOIN Employee_Phones ep ON e.EmpID = ep.EmpID " +
                    "LEFT JOIN Department d ON e.department_id = d.department_id " +
                    "LEFT JOIN Job_Role j ON e.role_id = j.role_id " +
                    "GROUP BY e.EmpID, e.Emp_name, e.Email, d.d_name, j.designation";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("EmpID"),
                        rs.getString("Emp_name"),
                        rs.getString("Email"),
                        rs.getString("phones"),
                        rs.getString("d_name"),
                        rs.getString("designation")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading employees");
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton addBtn = new JButton("Add Employee");
        JButton editBtn = new JButton("Edit Employee");
        JButton removeBtn = new JButton("Remove Employee");

        styleActionButton(addBtn, new Color(41, 128, 185));
        styleActionButton(editBtn, new Color(41, 128, 185));
        styleActionButton(removeBtn, new Color(192, 57, 43));

        addBtn.addActionListener(e -> addEmployee());
        editBtn.addActionListener(e -> editEmployee(table, model));
        removeBtn.addActionListener(e -> removeEmployee(table, model));

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(removeBtn);

        contentPanel.add(createSectionHeader("Employees", new Color(41, 128, 185)), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void showDepartments() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Head", "Employees"}, 0
        );
        JTable table = new JTable(model);
        styleTable(table);

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

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton addBtn = new JButton("Add Department");
        JButton editBtn = new JButton("Edit Department");
        JButton removeBtn = new JButton("Remove Department");

        styleActionButton(addBtn, new Color(155, 89, 182));
        styleActionButton(editBtn, new Color(155, 89, 182));
        styleActionButton(removeBtn, new Color(192, 57, 43));

        addBtn.addActionListener(e -> addDepartment());
        editBtn.addActionListener(e -> editDepartment(table, model));
        removeBtn.addActionListener(e -> removeDepartment(table, model));

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(removeBtn);

        contentPanel.add(createSectionHeader("Departments", new Color(155, 89, 182)), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void showRoles() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Role ID", "Designation", "Hours", "Base Salary", "Max Bonus", "Min Exp", "Job Type", "Total Leaves", "Department"}, 0
        );
        JTable table = new JTable(model);
        styleTable(table);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT jr.role_id, jr.designation, jr.work_hours, jr.base_salary, jr.max_bonus, " +
                    "jr.min_exp, jr.job_type, jr.total_leaves, d.d_name " +
                    "FROM Job_Role jr " +
                    "LEFT JOIN Department d ON jr.dept_id = d.department_id " +
                    "ORDER BY jr.role_id";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("role_id"),
                        rs.getString("designation"),
                        rs.getInt("work_hours"),
                        rs.getDouble("base_salary"),
                        rs.getDouble("max_bonus"),
                        rs.getInt("min_exp"),
                        rs.getString("job_type"),
                        rs.getInt("total_leaves"),
                        rs.getString("d_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading roles");
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton addBtn = new JButton("Add Role");
        JButton editBtn = new JButton("Edit Role");
        JButton removeBtn = new JButton("Remove Role");

        styleActionButton(addBtn, new Color(155, 89, 182));
        styleActionButton(editBtn, new Color(155, 89, 182));
        styleActionButton(removeBtn, new Color(192, 57, 43));

        addBtn.addActionListener(e -> addRole());
        editBtn.addActionListener(e -> editRole(table, model));
        removeBtn.addActionListener(e -> removeRole(table, model));

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(removeBtn);

        contentPanel.add(createSectionHeader("Roles", new Color(142, 68, 173)), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
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
        styleTable(table);

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
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton approveBtn = new JButton("Approve Selected");
        JButton rejectBtn = new JButton("Reject Selected");

        styleActionButton(approveBtn, new Color(39, 174, 96));
        styleActionButton(rejectBtn, new Color(231, 76, 60));

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

        contentPanel.add(createSectionHeader("Leave Requests", new Color(230, 126, 34)), BorderLayout.NORTH);
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
        styleTable(table);

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

        contentPanel.add(createSectionHeader("Attendance Logs", new Color(52, 152, 219)), BorderLayout.NORTH);
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
        styleTable(table);

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
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton generateBtn = new JButton("Generate Payroll");

        styleActionButton(generateBtn, new Color(46, 204, 113));

        generateBtn.addActionListener(e -> generatePayroll());

        bottomPanel.add(generateBtn);

        contentPanel.add(createSectionHeader("Payroll", new Color(46, 204, 113)), BorderLayout.NORTH);
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

            EmployeeSelection selectedEmployee = chooseEmployeeForPayroll(con);
            if (selectedEmployee == null) {
                return;
            }

            String empQuery = "SELECT e.Emp_name, COALESCE(j.base_salary, 0) AS base_salary " +
                    "FROM Employee e " +
                    "LEFT JOIN Job_Role j ON e.role_id = j.role_id " +
                    "WHERE e.EmpID = ?";

            try (PreparedStatement empPs = con.prepareStatement(empQuery);
                 PreparedStatement insertPs = con.prepareStatement(
                         "INSERT INTO Payroll (payroll_id, paydate, total_amount, transaction_id, EmpID) VALUES (?, ?, ?, ?, ?)"
                 )) {

                empPs.setInt(1, selectedEmployee.empId);
                try (ResultSet empRs = empPs.executeQuery()) {
                    if (!empRs.next()) {
                        JOptionPane.showMessageDialog(frame, "Employee not found.");
                        return;
                    }

                    String empName = empRs.getString("Emp_name");
                    double suggestedSalary = empRs.getDouble("base_salary");
                    Double salary = promptSalaryForEmployee(empName, suggestedSalary);

                    if (salary == null) {
                        return;
                    }

                    String transId = "TXN-" + selectedEmployee.empId + "-" + UUID.randomUUID().toString().substring(0, 8);

                    insertPs.setInt(1, nextId);
                    insertPs.setString(2, payDate);
                    insertPs.setDouble(3, salary);
                    insertPs.setString(4, transId);
                    insertPs.setInt(5, selectedEmployee.empId);
                    insertPs.executeUpdate();

                    JOptionPane.showMessageDialog(frame, "Payroll generated for " + empName + " (ID: " + selectedEmployee.empId + ")");
                }
            }
            showPayroll();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error generating payroll: " + e.getMessage());
        }
    }

    private EmployeeSelection chooseEmployeeForPayroll(Connection con) throws Exception {
        DefaultComboBoxModel<EmployeeSelection> model = new DefaultComboBoxModel<>();
        String query = "SELECT e.EmpID, e.Emp_name FROM Employee e ORDER BY e.EmpID";

        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addElement(new EmployeeSelection(rs.getInt("EmpID"), rs.getString("Emp_name")));
            }
        }

        if (model.getSize() == 0) {
            JOptionPane.showMessageDialog(frame, "No employees found.");
            return null;
        }

        JComboBox<EmployeeSelection> employeeBox = new JComboBox<>(model);
        employeeBox.setSelectedIndex(0);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JLabel("Select Employee:"), BorderLayout.NORTH);
        panel.add(employeeBox, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Select Employee for Payroll", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return (EmployeeSelection) employeeBox.getSelectedItem();
    }

    private Double promptSalaryForEmployee(String employeeName, double suggestedSalary) {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    frame,
                    "Enter salary for " + employeeName + ":",
                    String.valueOf(suggestedSalary)
            );

            if (input == null) {
                return null;
            }

            String trimmed = input.trim();
            if (trimmed.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Salary cannot be empty.");
                continue;
            }

            try {
                double salary = Double.parseDouble(trimmed);
                if (salary < 0) {
                    JOptionPane.showMessageDialog(frame, "Salary must be zero or greater.");
                    continue;
                }
                return salary;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid salary amount.");
            }
        }
    }

    private static class EmployeeSelection {
        private final int empId;
        private final String empName;

        private EmployeeSelection(int empId, String empName) {
            this.empId = empId;
            this.empName = empName;
        }

        @Override
        public String toString() {
            return empId + " - " + empName;
        }
    }

    private void showProjects() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Project ID", "Name", "Start", "End", "Status", "Department", "Team Lead"}, 0
        );
        JTable table = new JTable(model);
        styleTable(table);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT p.project_id, p.PName, p.StartDate, p.EndDate, p.Status, d.d_name, e.Emp_name " +
                    "FROM Projects p " +
                    "LEFT JOIN Employee e ON p.TeamLead = e.EmpID " +
                    "LEFT JOIN Department d ON p.dept_id = d.department_id";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("project_id"),
                        rs.getString("PName"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("Status"),
                        rs.getString("d_name"),
                        rs.getString("Emp_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading projects");
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton addBtn = new JButton("Add Project");
        JButton editBtn = new JButton("Edit Project");
        JButton manageTeamBtn = new JButton("Manage Team");
        JButton removeBtn = new JButton("Remove Project");

        styleActionButton(addBtn, new Color(26, 188, 156));
        styleActionButton(editBtn, new Color(26, 188, 156));
        styleActionButton(manageTeamBtn, new Color(155, 89, 182));
        styleActionButton(removeBtn, new Color(192, 57, 43));

        addBtn.addActionListener(e -> addProject());
        editBtn.addActionListener(e -> editProject(table, model));
        manageTeamBtn.addActionListener(e -> manageProjectEmployees(table, model));
        removeBtn.addActionListener(e -> removeProject(table, model));

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(manageTeamBtn);
        bottomPanel.add(removeBtn);

        contentPanel.add(createSectionHeader("Projects", new Color(26, 188, 156)), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void addProject() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField startField = new JTextField();
        JTextField endField = new JTextField();
        JTextField statusField = new JTextField("Active");
        JComboBox<String> deptBox = new JComboBox<>();
        JComboBox<String> leadBox = new JComboBox<>();

        // Populate department dropdown
        try (Connection con = DBConnection.getConnection()) {
            String deptQuery = "SELECT department_id, d_name FROM Department ORDER BY d_name";
            PreparedStatement deptPs = con.prepareStatement(deptQuery);
            ResultSet deptRs = deptPs.executeQuery();
            while (deptRs.next()) {
                deptBox.addItem(deptRs.getInt("department_id") + " - " + deptRs.getString("d_name"));
            }

            // Populate team lead dropdown
            String leadQuery = "SELECT EmpID, Emp_name FROM Employee ORDER BY Emp_name";
            PreparedStatement leadPs = con.prepareStatement(leadQuery);
            ResultSet leadRs = leadPs.executeQuery();
            while (leadRs.next()) {
                leadBox.addItem(leadRs.getInt("EmpID") + " - " + leadRs.getString("Emp_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading data: " + e.getMessage());
            return;
        }

        panel.add(new JLabel("Project Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Department:"));
        panel.add(deptBox);
        panel.add(new JLabel("Team Lead:"));
        panel.add(leadBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Project", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Project Name is required");
            return;
        }
        if (startField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Start Date is required");
            return;
        }
        if (endField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "End Date is required");
            return;
        }
        if (deptBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(frame, "Department is required");
            return;
        }
        if (leadBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(frame, "Team Lead is required");
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

            // Extract department ID from selected item
            String deptSelection = (String) deptBox.getSelectedItem();
            int deptId = Integer.parseInt(deptSelection.split(" - ")[0]);

            // Extract team lead ID from selected item
            String leadSelection = (String) leadBox.getSelectedItem();
            int teamLeadId = Integer.parseInt(leadSelection.split(" - ")[0]);

            String insertQuery = "INSERT INTO Projects (project_id, PName, StartDate, EndDate, Status, TeamLead, dept_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setInt(1, nextId);
            ps.setString(2, nameField.getText().trim());
            ps.setString(3, startField.getText());
            ps.setString(4, endField.getText());
            ps.setString(5, statusField.getText());
            ps.setInt(6, teamLeadId);
            ps.setInt(7, deptId);
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
            new String[]{"Meeting ID", "Date", "Time", "Topic", "Department"}, 0
        );
        JTable table = new JTable(model);
        styleTable(table);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT m.meeting_id, m.m_date, m.m_time, m.topic, m.dept_id, d.d_name " +
                    "FROM Meeting m " +
                    "LEFT JOIN Department d ON m.dept_id = d.department_id " +
                    "ORDER BY m.m_date DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                rs.getInt("meeting_id"),
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
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton addBtn = new JButton("Add Meeting");
        JButton editBtn = new JButton("Edit Meeting");
        JButton removeBtn = new JButton("Remove Meeting");

        styleActionButton(addBtn, new Color(231, 76, 60));
        styleActionButton(editBtn, new Color(231, 76, 60));
        styleActionButton(removeBtn, new Color(192, 57, 43));

        addBtn.addActionListener(e -> addMeeting());
        editBtn.addActionListener(e -> editMeeting(table, model));
        removeBtn.addActionListener(e -> removeMeeting(table, model));

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(removeBtn);

        contentPanel.add(createSectionHeader("Meetings", new Color(231, 76, 60)), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        refresh();
    }

    private void editProject(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a project");
            return;
        }

        int projectId = (int) model.getValueAt(row, 0);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement fetchPs = con.prepareStatement(
                     "SELECT PName, StartDate, EndDate, Status, TeamLead, dept_id FROM Projects WHERE project_id = ?")) {
            fetchPs.setInt(1, projectId);
            ResultSet rs = fetchPs.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(frame, "Project not found");
                return;
            }

            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            JTextField nameField = new JTextField(rs.getString("PName"));
            JTextField startField = new JTextField(rs.getDate("StartDate") == null ? "" : rs.getDate("StartDate").toString());
            JTextField endField = new JTextField(rs.getDate("EndDate") == null ? "" : rs.getDate("EndDate").toString());
            JTextField statusField = new JTextField(rs.getString("Status"));
            JComboBox<String> deptBox = new JComboBox<>();
            JComboBox<String> leadBox = new JComboBox<>();

            // Populate department dropdown
            String deptQuery = "SELECT department_id, d_name FROM Department ORDER BY d_name";
            PreparedStatement deptPs = con.prepareStatement(deptQuery);
            ResultSet deptRs = deptPs.executeQuery();
            while (deptRs.next()) {
                String deptItem = deptRs.getInt("department_id") + " - " + deptRs.getString("d_name");
                deptBox.addItem(deptItem);
                // Select current department
                if (deptRs.getInt("department_id") == rs.getInt("dept_id")) {
                    deptBox.setSelectedItem(deptItem);
                }
            }

            // Populate team lead dropdown
            String leadQuery = "SELECT EmpID, Emp_name FROM Employee ORDER BY Emp_name";
            PreparedStatement leadPs = con.prepareStatement(leadQuery);
            ResultSet leadRs = leadPs.executeQuery();
            while (leadRs.next()) {
                String leadItem = leadRs.getInt("EmpID") + " - " + leadRs.getString("Emp_name");
                leadBox.addItem(leadItem);
                // Select current team lead
                if (leadRs.getInt("EmpID") == rs.getInt("TeamLead")) {
                    leadBox.setSelectedItem(leadItem);
                }
            }

            panel.add(new JLabel("Project Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
            panel.add(startField);
            panel.add(new JLabel("End Date (YYYY-MM-DD):"));
            panel.add(endField);
            panel.add(new JLabel("Status:"));
            panel.add(statusField);
            panel.add(new JLabel("Department:"));
            panel.add(deptBox);
            panel.add(new JLabel("Team Lead:"));
            panel.add(leadBox);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Project #" + projectId, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            // Validate required fields
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Project Name is required");
                return;
            }
            if (startField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Start Date is required");
                return;
            }
            if (endField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "End Date is required");
                return;
            }
            if (deptBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(frame, "Department is required");
                return;
            }
            if (leadBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(frame, "Team Lead is required");
                return;
            }

            // Extract department ID from selected item
            String deptSelection = (String) deptBox.getSelectedItem();
            int deptId = Integer.parseInt(deptSelection.split(" - ")[0]);

            // Extract team lead ID from selected item
            String leadSelection = (String) leadBox.getSelectedItem();
            int teamLeadId = Integer.parseInt(leadSelection.split(" - ")[0]);

            try (PreparedStatement updatePs = con.prepareStatement(
                    "UPDATE Projects SET PName = ?, StartDate = ?, EndDate = ?, Status = ?, TeamLead = ?, dept_id = ? WHERE project_id = ?")) {
                updatePs.setString(1, nameField.getText().trim());
                updatePs.setString(2, startField.getText().trim());
                updatePs.setString(3, endField.getText().trim());
                updatePs.setString(4, statusField.getText().trim());
                updatePs.setInt(5, teamLeadId);
                updatePs.setInt(6, deptId);
                updatePs.setInt(7, projectId);
                updatePs.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame, "Project updated successfully");
            showProjects();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating project: " + e.getMessage());
        }
    }

    private void removeProject(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a project");
            return;
        }

        int projectId = (int) model.getValueAt(row, 0);
        int result = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to remove project " + projectId + "?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement deleteAssignments = con.prepareStatement("DELETE FROM Employee_Projects WHERE project_id = ?")) {
                deleteAssignments.setInt(1, projectId);
                deleteAssignments.executeUpdate();
            }

            try (PreparedStatement deleteProject = con.prepareStatement("DELETE FROM Projects WHERE project_id = ?")) {
                deleteProject.setInt(1, projectId);
                int rows = deleteProject.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Project removed successfully");
                    showProjects();
                } else {
                    JOptionPane.showMessageDialog(frame, "Project not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing project: " + e.getMessage());
        }
    }

    private void manageProjectEmployees(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a project");
            return;
        }

        int projectId = (int) model.getValueAt(row, 0);
        String projectName = (String) model.getValueAt(row, 1);

        showProjectEmployeesDialog(projectId, projectName);
    }

    private void showProjectEmployeesDialog(int projectId, String projectName) {
        JDialog dialog = new JDialog(frame, "Manage Team - " + projectName, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        // Display current team members
        DefaultTableModel teamModel = new DefaultTableModel(
                new String[]{"Employee ID", "Name", "Role in Project", "Hours/Week", "Assigned Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable teamTable = new JTable(teamModel);
        styleTable(teamTable);

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT ep.EmpID, e.Emp_name, ep.role_in_project, ep.hours_per_week, ep.assigned_date " +
                    "FROM Employee_Projects ep " +
                    "LEFT JOIN Employee e ON ep.EmpID = e.EmpID " +
                    "WHERE ep.project_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                teamModel.addRow(new Object[]{
                        rs.getInt("EmpID"),
                        rs.getString("Emp_name"),
                        rs.getString("role_in_project"),
                        rs.getInt("hours_per_week"),
                        rs.getDate("assigned_date")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error loading team members");
        }

        // Create action panel
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(new Color(236, 240, 241));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton addEmpBtn = new JButton("Add Employee");
        JButton removeEmpBtn = new JButton("Remove Employee");
        JButton closeBtn = new JButton("Close");

        styleActionButton(addEmpBtn, new Color(26, 188, 156));
        styleActionButton(removeEmpBtn, new Color(192, 57, 43));
        styleActionButton(closeBtn, new Color(52, 152, 219));

        addEmpBtn.addActionListener(e -> addEmployeeToProject(projectId, dialog, teamModel));
        removeEmpBtn.addActionListener(e -> {
            int selectedRow = teamTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select an employee");
                return;
            }
            int empId = (int) teamModel.getValueAt(selectedRow, 0);
            removeEmployeeFromProject(projectId, empId, dialog, teamModel);
        });
        closeBtn.addActionListener(e -> dialog.dispose());

        actionPanel.add(addEmpBtn);
        actionPanel.add(removeEmpBtn);
        actionPanel.add(closeBtn);

        dialog.add(createSectionHeader("Team Members", new Color(26, 188, 156)), BorderLayout.NORTH);
        dialog.add(new JScrollPane(teamTable), BorderLayout.CENTER);
        dialog.add(actionPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addEmployeeToProject(int projectId, JDialog parentDialog, DefaultTableModel teamModel) {
        try (Connection con = DBConnection.getConnection()) {
            // Get list of employees not yet in the project
            String query = "SELECT e.EmpID, e.Emp_name FROM Employee e " +
                    "WHERE e.EmpID NOT IN (SELECT EmpID FROM Employee_Projects WHERE project_id = ?) " +
                    "ORDER BY e.EmpID";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();

            DefaultComboBoxModel<EmployeeSelection> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                model.addElement(new EmployeeSelection(rs.getInt("EmpID"), rs.getString("Emp_name")));
            }

            if (model.getSize() == 0) {
                JOptionPane.showMessageDialog(parentDialog, "All employees are already added to this project");
                return;
            }

            JComboBox<EmployeeSelection> employeeBox = new JComboBox<>(model);
            employeeBox.setSelectedIndex(0);

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField roleField = new JTextField("Developer");
            JTextField hoursField = new JTextField("40");

            panel.add(new JLabel("Select Employee:"));
            panel.add(employeeBox);
            panel.add(new JLabel("Role in Project:"));
            panel.add(roleField);
            panel.add(new JLabel("Hours per Week:"));
            panel.add(hoursField);

            int result = JOptionPane.showConfirmDialog(parentDialog, panel, "Add Employee to Project", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            EmployeeSelection selected = (EmployeeSelection) employeeBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(parentDialog, "Please select an employee");
                return;
            }

            try (PreparedStatement insertPs = con.prepareStatement(
                    "INSERT INTO Employee_Projects (EmpID, project_id, assigned_date, role_in_project, hours_per_week) VALUES (?, ?, CURDATE(), ?, ?)")) {
                insertPs.setInt(1, selected.empId);
                insertPs.setInt(2, projectId);
                insertPs.setString(3, roleField.getText().trim());
                insertPs.setInt(4, Integer.parseInt(hoursField.getText().trim()));
                insertPs.executeUpdate();

                // Refresh the team table
                teamModel.setRowCount(0);
                String refreshQuery = "SELECT ep.EmpID, e.Emp_name, ep.role_in_project, ep.hours_per_week, ep.assigned_date " +
                        "FROM Employee_Projects ep " +
                        "LEFT JOIN Employee e ON ep.EmpID = e.EmpID " +
                        "WHERE ep.project_id = ?";
                PreparedStatement refreshPs = con.prepareStatement(refreshQuery);
                refreshPs.setInt(1, projectId);
                ResultSet refreshRs = refreshPs.executeQuery();

                while (refreshRs.next()) {
                    teamModel.addRow(new Object[]{
                            refreshRs.getInt("EmpID"),
                            refreshRs.getString("Emp_name"),
                            refreshRs.getString("role_in_project"),
                            refreshRs.getInt("hours_per_week"),
                            refreshRs.getDate("assigned_date")
                    });
                }

                JOptionPane.showMessageDialog(parentDialog, "Employee added to project successfully");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentDialog, "Hours per week must be a valid number");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentDialog, "Error adding employee: " + e.getMessage());
        }
    }

    private void removeEmployeeFromProject(int projectId, int empId, JDialog parentDialog, DefaultTableModel teamModel) {
        int result = JOptionPane.showConfirmDialog(parentDialog,
                "Are you sure you want to remove this employee from the project?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement deletePs = con.prepareStatement("DELETE FROM Employee_Projects WHERE EmpID = ? AND project_id = ?")) {
            deletePs.setInt(1, empId);
            deletePs.setInt(2, projectId);
            deletePs.executeUpdate();

            // Refresh the team table
            teamModel.setRowCount(0);
            String query = "SELECT ep.EmpID, e.Emp_name, ep.role_in_project, ep.hours_per_week, ep.assigned_date " +
                    "FROM Employee_Projects ep " +
                    "LEFT JOIN Employee e ON ep.EmpID = e.EmpID " +
                    "WHERE ep.project_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                teamModel.addRow(new Object[]{
                        rs.getInt("EmpID"),
                        rs.getString("Emp_name"),
                        rs.getString("role_in_project"),
                        rs.getInt("hours_per_week"),
                        rs.getDate("assigned_date")
                });
            }

            JOptionPane.showMessageDialog(parentDialog, "Employee removed from project successfully");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentDialog, "Error removing employee: " + e.getMessage());
        }
    }

    private void editMeeting(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a meeting");
            return;
        }

        int meetingId = (int) model.getValueAt(row, 0);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement fetchPs = con.prepareStatement(
                     "SELECT m_date, m_time, topic, dept_id FROM Meeting WHERE meeting_id = ?")) {
            fetchPs.setInt(1, meetingId);
            ResultSet rs = fetchPs.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(frame, "Meeting not found");
                return;
            }

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField dateField = new JTextField(rs.getDate("m_date") == null ? "" : rs.getDate("m_date").toString());
            JTextField timeField = new JTextField(rs.getTime("m_time") == null ? "" : rs.getTime("m_time").toString());
            JTextField topicField = new JTextField(rs.getString("topic"));
            JComboBox<String> deptBox = new JComboBox<>();

            // Populate department dropdown
            String deptQuery = "SELECT department_id, d_name FROM Department ORDER BY d_name";
            PreparedStatement deptPs = con.prepareStatement(deptQuery);
            ResultSet deptRs = deptPs.executeQuery();
            while (deptRs.next()) {
                String deptItem = deptRs.getInt("department_id") + " - " + deptRs.getString("d_name");
                deptBox.addItem(deptItem);
                // Select current department
                if (deptRs.getInt("department_id") == rs.getInt("dept_id")) {
                    deptBox.setSelectedItem(deptItem);
                }
            }

            panel.add(new JLabel("Date (YYYY-MM-DD):"));
            panel.add(dateField);
            panel.add(new JLabel("Time (HH:MM:SS):"));
            panel.add(timeField);
            panel.add(new JLabel("Topic:"));
            panel.add(topicField);
            panel.add(new JLabel("Department:"));
            panel.add(deptBox);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Meeting #" + meetingId, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            // Validate required fields
            if (dateField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Date is required");
                return;
            }
            if (timeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Time is required");
                return;
            }
            if (topicField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Topic is required");
                return;
            }
            if (deptBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(frame, "Department is required");
                return;
            }

            // Extract department ID from selected item
            String deptSelection = (String) deptBox.getSelectedItem();
            int deptId = Integer.parseInt(deptSelection.split(" - ")[0]);

            try (PreparedStatement updatePs = con.prepareStatement(
                    "UPDATE Meeting SET m_date = ?, m_time = ?, topic = ?, dept_id = ? WHERE meeting_id = ?")) {
                updatePs.setString(1, dateField.getText().trim());
                updatePs.setString(2, timeField.getText().trim());
                updatePs.setString(3, topicField.getText().trim());
                updatePs.setInt(4, deptId);
                updatePs.setInt(5, meetingId);
                updatePs.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame, "Meeting updated successfully");
            showMeetings();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating meeting: " + e.getMessage());
        }
    }

    private void removeMeeting(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a meeting");
            return;
        }

        int meetingId = (int) model.getValueAt(row, 0);
        int result = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to remove meeting " + meetingId + "?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement deleteMeeting = con.prepareStatement("DELETE FROM Meeting WHERE meeting_id = ?")) {
            deleteMeeting.setInt(1, meetingId);
            int rows = deleteMeeting.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Meeting removed successfully");
                showMeetings();
            } else {
                JOptionPane.showMessageDialog(frame, "Meeting not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing meeting: " + e.getMessage());
        }
    }

    private void addMeeting() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField topicField = new JTextField();
        JComboBox<String> deptBox = new JComboBox<>();

        // Populate department dropdown
        try (Connection con = DBConnection.getConnection()) {
            String deptQuery = "SELECT department_id, d_name FROM Department ORDER BY d_name";
            PreparedStatement deptPs = con.prepareStatement(deptQuery);
            ResultSet deptRs = deptPs.executeQuery();
            while (deptRs.next()) {
                deptBox.addItem(deptRs.getInt("department_id") + " - " + deptRs.getString("d_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading departments: " + e.getMessage());
            return;
        }

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM:SS):"));
        panel.add(timeField);
        panel.add(new JLabel("Topic:"));
        panel.add(topicField);
        panel.add(new JLabel("Department:"));
        panel.add(deptBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Meeting", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Validate required fields
        if (dateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Date is required");
            return;
        }
        if (timeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Time is required");
            return;
        }
        if (topicField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Topic is required");
            return;
        }
        if (deptBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(frame, "Department is required");
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

            // Extract department ID from selected item
            String deptSelection = (String) deptBox.getSelectedItem();
            int deptId = Integer.parseInt(deptSelection.split(" - ")[0]);

            String insertQuery = "INSERT INTO Meeting (meeting_id, m_date, m_time, topic, dept_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setInt(1, nextId);
            ps.setString(2, dateField.getText());
            ps.setString(3, timeField.getText());
            ps.setString(4, topicField.getText());
            ps.setInt(5, deptId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Meeting added successfully");
            showMeetings();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding meeting: " + e.getMessage());
        }
    }

    @Override
    public void changePassword() {
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
        JPanel panel = new JPanel(new GridLayout(12, 2, 5, 5));
        JTextField idField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField nameField = new JTextField();
        JComboBox<String> genderField = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField emailField = new JTextField();
        JTextField dobField = new JTextField("YYYY-MM-DD");
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField phoneField2 = new JTextField();
        JComboBox<IdNameOption> deptField = new JComboBox<>();
        JComboBox<IdNameOption> roleField = new JComboBox<>();
        JPasswordField passwordField = new JPasswordField();

        try (Connection con = DBConnection.getConnection()) {
            loadDepartmentOptions(con, deptField);
            loadRoleOptions(con, roleField);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading departments/roles: " + e.getMessage());
            return;
        }

        if (deptField.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No departments available. Please add a department first.");
            return;
        }

        if (roleField.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No roles available. Please add a role first.");
            return;
        }

        panel.add(new JLabel("Employee ID:"));
        panel.add(idField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("DOB (YYYY-MM-DD):"));
        panel.add(dobField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Phone Number 1:"));
        panel.add(phoneField);
        panel.add(new JLabel("Phone Number 2:"));
        panel.add(phoneField2);
        panel.add(new JLabel("Department:"));
        panel.add(deptField);
        panel.add(new JLabel("Role:"));
        panel.add(roleField);
        panel.add(new JLabel("Login Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String usernameInput = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (usernameInput.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username is required");
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Login password is required");
            return;
        }
        if (password.length() < 3) {
            JOptionPane.showMessageDialog(frame, "Login password must be at least 3 characters long");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            int empId = Integer.parseInt(idField.getText().trim());
            IdNameOption selectedDepartment = (IdNameOption) deptField.getSelectedItem();
            IdNameOption selectedRole = (IdNameOption) roleField.getSelectedItem();
            String username = usernameInput;

            if (selectedDepartment == null || selectedRole == null) {
                JOptionPane.showMessageDialog(frame, "Please select both department and role");
                return;
            }

            PreparedStatement checkUserPs = con.prepareStatement(
                    "SELECT id FROM users WHERE username = ?"
            );
            checkUserPs.setString(1, username);
            ResultSet existingUserRs = checkUserPs.executeQuery();
            if (existingUserRs.next()) {
                JOptionPane.showMessageDialog(frame, "Username already exists. Please choose another.");
                return;
            }

            String insertQuery = "INSERT INTO Employee (EmpID, Emp_name, Gender, DOB, Email, Street, department_id, role_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setInt(1, empId);
            ps.setString(2, nameField.getText());
            ps.setString(3, (String) genderField.getSelectedItem());
            ps.setString(4, dobField.getText());
            ps.setString(5, emailField.getText());
            ps.setString(6, addressField.getText());
            ps.setInt(7, selectedDepartment.id);
            ps.setInt(8, selectedRole.id);
            
            ps.executeUpdate();

            // Insert phone numbers if provided
            String phoneNumber1 = phoneField.getText().trim();
            String phoneNumber2 = phoneField2.getText().trim();

            try {
                if (!phoneNumber1.isEmpty()) {
                    validatePhoneNumber(phoneNumber1, "Phone Number 1");
                }

                if (!phoneNumber2.isEmpty()) {
                    validatePhoneNumber(phoneNumber2, "Phone Number 2");
                }
            } catch (PhoneNumberValidationException e) {
                JOptionPane.showMessageDialog(frame, e.getMessage());
                return;
            }
            
            if (!phoneNumber1.isEmpty()) {
                PreparedStatement phonePs = con.prepareStatement(
                    "INSERT INTO Employee_Phones (EmpID, Phone_Number) VALUES (?, ?)"
                );
                phonePs.setInt(1, empId);
                phonePs.setString(2, phoneNumber1);
                phonePs.executeUpdate();
            }
            
            if (!phoneNumber2.isEmpty()) {
                PreparedStatement phonePs2 = con.prepareStatement(
                    "INSERT INTO Employee_Phones (EmpID, Phone_Number) VALUES (?, ?)"
                );
                phonePs2.setInt(1, empId);
                phonePs2.setString(2, phoneNumber2);
                phonePs2.executeUpdate();
            }

            PreparedStatement userPs = con.prepareStatement(
                "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)"
            );
            userPs.setInt(1, empId);
            userPs.setString(2, username);
            userPs.setString(3, password);
                userPs.setString(4, "employee");
            userPs.executeUpdate();

            con.commit();
            con.setAutoCommit(true);

            JOptionPane.showMessageDialog(frame, "Employee added successfully. Username: " + username);
            showEmployees();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Employee ID must be numeric");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding employee: " + e.getMessage());
        }
    }

    private void loadDepartmentOptions(Connection con, JComboBox<IdNameOption> departmentBox) throws Exception {
        String query = "SELECT department_id, d_name FROM Department ORDER BY department_id";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                departmentBox.addItem(new IdNameOption(rs.getInt("department_id"), rs.getString("d_name")));
            }
        }
    }

    private void loadRoleOptions(Connection con, JComboBox<IdNameOption> roleBox) throws Exception {
        String query = "SELECT role_id, designation FROM Job_Role ORDER BY role_id";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roleBox.addItem(new IdNameOption(rs.getInt("role_id"), rs.getString("designation")));
            }
        }
    }

    private static class IdNameOption {
        private final int id;
        private final String name;

        private IdNameOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return id + " - " + name;
        }
    }

    private void removeEmployee(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select an employee");
            return;
        }

        int empId = (int) model.getValueAt(row, 0);

        int result = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to remove employee " + empId + "?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement deleteSalaryBreakdownPs = con.prepareStatement(
                        "DELETE FROM Salary_Breakdown WHERE payroll_id IN (SELECT payroll_id FROM Payroll WHERE EmpID = ?)"
                )) {
                    deleteSalaryBreakdownPs.setInt(1, empId);
                    deleteSalaryBreakdownPs.executeUpdate();
                }

                try (PreparedStatement deletePayrollPs = con.prepareStatement(
                        "DELETE FROM Payroll WHERE EmpID = ?"
                )) {
                    deletePayrollPs.setInt(1, empId);
                    deletePayrollPs.executeUpdate();
                }

                try (PreparedStatement deleteEmployeeProjectLinksPs = con.prepareStatement(
                        "DELETE FROM Employee_Projects WHERE EmpID = ? OR project_id IN (SELECT project_id FROM Projects WHERE TeamLead = ?)"
                )) {
                    deleteEmployeeProjectLinksPs.setInt(1, empId);
                    deleteEmployeeProjectLinksPs.setInt(2, empId);
                    deleteEmployeeProjectLinksPs.executeUpdate();
                }

                try (PreparedStatement deleteProjectsPs = con.prepareStatement(
                        "DELETE FROM Projects WHERE TeamLead = ?"
                )) {
                    deleteProjectsPs.setInt(1, empId);
                    deleteProjectsPs.executeUpdate();
                }

                try (PreparedStatement deleteAttendancePs = con.prepareStatement(
                        "DELETE FROM Attendance_Log WHERE EmpID = ?"
                )) {
                    deleteAttendancePs.setInt(1, empId);
                    deleteAttendancePs.executeUpdate();
                }

                try (PreparedStatement deleteSickLeavePs = con.prepareStatement(
                        "DELETE FROM Sick_Leave WHERE leave_id IN (SELECT leave_id FROM Leave_Request WHERE EmpID = ?)"
                )) {
                    deleteSickLeavePs.setInt(1, empId);
                    deleteSickLeavePs.executeUpdate();
                }

                try (PreparedStatement deleteCasualLeavePs = con.prepareStatement(
                        "DELETE FROM Casual_Leave WHERE leave_id IN (SELECT leave_id FROM Leave_Request WHERE EmpID = ?)"
                )) {
                    deleteCasualLeavePs.setInt(1, empId);
                    deleteCasualLeavePs.executeUpdate();
                }

                try (PreparedStatement deletePaidLeavePs = con.prepareStatement(
                        "DELETE FROM Paid_Leave WHERE leave_id IN (SELECT leave_id FROM Leave_Request WHERE EmpID = ?)"
                )) {
                    deletePaidLeavePs.setInt(1, empId);
                    deletePaidLeavePs.executeUpdate();
                }

                try (PreparedStatement deleteLeavePs = con.prepareStatement(
                        "DELETE FROM Leave_Request WHERE EmpID = ?"
                )) {
                    deleteLeavePs.setInt(1, empId);
                    deleteLeavePs.executeUpdate();
                }

                try (PreparedStatement deleteRecruitmentPs = con.prepareStatement(
                        "DELETE FROM Recruitment WHERE EmpID = ? OR recruiter_id = ?"
                )) {
                    deleteRecruitmentPs.setInt(1, empId);
                    deleteRecruitmentPs.setInt(2, empId);
                    deleteRecruitmentPs.executeUpdate();
                }

                try (PreparedStatement deleteInternPs = con.prepareStatement(
                        "DELETE FROM Intern WHERE EmpID = ?"
                )) {
                    deleteInternPs.setInt(1, empId);
                    deleteInternPs.executeUpdate();
                }

                try (PreparedStatement deleteFullTimePs = con.prepareStatement(
                        "DELETE FROM Full_Time WHERE EmpID = ?"
                )) {
                    deleteFullTimePs.setInt(1, empId);
                    deleteFullTimePs.executeUpdate();
                }

                try (PreparedStatement deleteBranchDeptPs = con.prepareStatement(
                        "DELETE FROM Branch_Dept WHERE branch_id IN (SELECT branch_id FROM Branch WHERE mgr_id = ?)"
                )) {
                    deleteBranchDeptPs.setInt(1, empId);
                    deleteBranchDeptPs.executeUpdate();
                }

                try (PreparedStatement deleteBranchesPs = con.prepareStatement(
                        "DELETE FROM Branch WHERE mgr_id = ?"
                )) {
                    deleteBranchesPs.setInt(1, empId);
                    deleteBranchesPs.executeUpdate();
                }

                try (PreparedStatement deletePhonesPs = con.prepareStatement(
                        "DELETE FROM Employee_Phones WHERE EmpID = ?"
                )) {
                    deletePhonesPs.setInt(1, empId);
                    deletePhonesPs.executeUpdate();
                }

                try (PreparedStatement deleteUserCredsPs = con.prepareStatement(
                        "DELETE FROM user_credentials WHERE EmpID = ?"
                )) {
                    deleteUserCredsPs.setInt(1, empId);
                    deleteUserCredsPs.executeUpdate();
                }

                int rows;
                try (PreparedStatement deleteEmpPs = con.prepareStatement(
                        "DELETE FROM Employee WHERE EmpID = ?"
                )) {
                    deleteEmpPs.setInt(1, empId);
                    rows = deleteEmpPs.executeUpdate();
                }

                if (rows > 0) {
                    try (PreparedStatement deleteUserPs = con.prepareStatement(
                            "DELETE FROM users WHERE id = ? AND role = ?"
                    )) {
                        deleteUserPs.setInt(1, empId);
                        deleteUserPs.setString(2, "employee");
                        deleteUserPs.executeUpdate();
                    }
                }

                con.commit();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Employee removed successfully");
                    showEmployees();
                } else {
                    JOptionPane.showMessageDialog(frame, "Employee not found");
                }
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing employee: " + e.getMessage());
        }
    }

    private void editEmployee(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select an employee");
            return;
        }

        int empId = (int) model.getValueAt(row, 0);

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement fetchPs = con.prepareStatement(
                    "SELECT Emp_name, Gender, DOB, Email, Street, department_id, role_id FROM Employee WHERE EmpID = ?"
            );
            fetchPs.setInt(1, empId);
            ResultSet rs = fetchPs.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(frame, "Employee not found");
                return;
            }

            JPanel panel = new JPanel(new GridLayout(11, 2, 5, 5));
            JTextField nameField = new JTextField(rs.getString("Emp_name"));
            JComboBox<String> genderField = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            genderField.setSelectedItem(rs.getString("Gender"));
            JTextField dobField = new JTextField(rs.getDate("DOB") != null ? rs.getDate("DOB").toString() : "");
            JTextField emailField = new JTextField(rs.getString("Email"));
            JTextField addressField = new JTextField(rs.getString("Street"));
            JTextField phoneField1 = new JTextField();
            JTextField phoneField2 = new JTextField();
            JComboBox<IdNameOption> deptField = new JComboBox<>();
            JComboBox<IdNameOption> roleField = new JComboBox<>();
            JPasswordField passwordField = new JPasswordField();
            JTextField usernameField = new JTextField();

            loadDepartmentOptions(con, deptField);
            loadRoleOptions(con, roleField);

            if (deptField.getItemCount() == 0) {
                JOptionPane.showMessageDialog(frame, "No departments available. Please add a department first.");
                return;
            }

            if (roleField.getItemCount() == 0) {
                JOptionPane.showMessageDialog(frame, "No roles available. Please add a role first.");
                return;
            }

            int currentDepartmentId = rs.getObject("department_id") == null ? -1 : rs.getInt("department_id");
            int currentRoleId = rs.getObject("role_id") == null ? -1 : rs.getInt("role_id");

            for (int i = 0; i < deptField.getItemCount(); i++) {
                IdNameOption option = deptField.getItemAt(i);
                if (option.id == currentDepartmentId) {
                    deptField.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < roleField.getItemCount(); i++) {
                IdNameOption option = roleField.getItemAt(i);
                if (option.id == currentRoleId) {
                    roleField.setSelectedIndex(i);
                    break;
                }
            }

            PreparedStatement userFetchPs = con.prepareStatement(
                    "SELECT username FROM users WHERE id = ? AND role = ?"
            );
            userFetchPs.setInt(1, empId);
            userFetchPs.setString(2, "employee");
            ResultSet userRs = userFetchPs.executeQuery();
            if (userRs.next()) {
                usernameField.setText(userRs.getString("username"));
            }

            // Fetch existing phone numbers
            PreparedStatement phoneFetchPs = con.prepareStatement(
                    "SELECT Phone_Number FROM Employee_Phones WHERE EmpID = ? ORDER BY Phone_Number"
            );
            phoneFetchPs.setInt(1, empId);
            ResultSet phoneRs = phoneFetchPs.executeQuery();
            String[] phoneNumbers = new String[2];
            int phoneCount = 0;
            while (phoneRs.next() && phoneCount < 2) {
                phoneNumbers[phoneCount] = phoneRs.getString("Phone_Number");
                phoneCount++;
            }
            if (phoneCount > 0) phoneField1.setText(phoneNumbers[0]);
            if (phoneCount > 1) phoneField2.setText(phoneNumbers[1]);

            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Gender:"));
            panel.add(genderField);
            panel.add(new JLabel("DOB (YYYY-MM-DD):"));
            panel.add(dobField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            panel.add(new JLabel("Phone Number 1:"));
            panel.add(phoneField1);
            panel.add(new JLabel("Phone Number 2:"));
            panel.add(phoneField2);
            panel.add(new JLabel("Department:"));
            panel.add(deptField);
            panel.add(new JLabel("Role:"));
            panel.add(roleField);
            panel.add(new JLabel("New Password (optional):"));
            panel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Employee #" + empId, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            IdNameOption selectedDepartment = (IdNameOption) deptField.getSelectedItem();
            IdNameOption selectedRole = (IdNameOption) roleField.getSelectedItem();

            if (selectedDepartment == null || selectedRole == null) {
                JOptionPane.showMessageDialog(frame, "Please select both department and role");
                return;
            }

            String phone1 = phoneField1.getText().trim();
            String phone2 = phoneField2.getText().trim();
            java.util.LinkedHashSet<String> phoneNumbersToSave = new java.util.LinkedHashSet<>();

            try {
                if (!phone1.isEmpty()) {
                    validatePhoneNumber(phone1, "Phone Number 1");
                    phoneNumbersToSave.add(phone1);
                }

                if (!phone2.isEmpty()) {
                    validatePhoneNumber(phone2, "Phone Number 2");
                    if (!phoneNumbersToSave.add(phone2)) {
                        JOptionPane.showMessageDialog(frame, "Phone numbers must be unique");
                        return;
                    }
                }
            } catch (PhoneNumberValidationException e) {
                JOptionPane.showMessageDialog(frame, e.getMessage());
                return;
            }

            String updatedUsername = usernameField.getText().trim();
            if (updatedUsername.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username is required");
                return;
            }

            PreparedStatement checkUserPs = con.prepareStatement(
                    "SELECT id FROM users WHERE username = ? AND id <> ?"
            );
            checkUserPs.setString(1, updatedUsername);
            checkUserPs.setInt(2, empId);
            ResultSet usernameConflictRs = checkUserPs.executeQuery();
            if (usernameConflictRs.next()) {
                JOptionPane.showMessageDialog(frame, "Username already exists. Please choose another.");
                return;
            }

            con.setAutoCommit(false);

            try {
                int rows;
                PreparedStatement updatePs = con.prepareStatement(
                        "UPDATE Employee SET Emp_name = ?, Gender = ?, DOB = ?, Email = ?, Street = ?, department_id = ?, role_id = ? WHERE EmpID = ?"
                );
                updatePs.setString(1, nameField.getText().trim());
                updatePs.setString(2, (String) genderField.getSelectedItem());
                updatePs.setString(3, dobField.getText().trim().isEmpty() ? null : dobField.getText().trim());
                updatePs.setString(4, emailField.getText().trim());
                updatePs.setString(5, addressField.getText().trim());
                updatePs.setInt(6, selectedDepartment.id);
                updatePs.setInt(7, selectedRole.id);
                updatePs.setInt(8, empId);
                rows = updatePs.executeUpdate();

                PreparedStatement deletePhonePs = con.prepareStatement(
                        "DELETE FROM Employee_Phones WHERE EmpID = ?"
                );
                deletePhonePs.setInt(1, empId);
                deletePhonePs.executeUpdate();

                for (String phoneNumber : phoneNumbersToSave) {
                    try (PreparedStatement insertPhonePs = con.prepareStatement(
                            "INSERT INTO Employee_Phones (EmpID, Phone_Number) VALUES (?, ?)"
                    )) {
                        insertPhonePs.setInt(1, empId);
                        insertPhonePs.setString(2, phoneNumber);
                        insertPhonePs.executeUpdate();
                    }
                }

                PreparedStatement updateUsernamePs = con.prepareStatement(
                        "UPDATE users SET username = ? WHERE id = ? AND role = ?"
                );
                updateUsernamePs.setString(1, updatedUsername);
                updateUsernamePs.setInt(2, empId);
                updateUsernamePs.setString(3, "employee");
                int usernameRows = updateUsernamePs.executeUpdate();

                if (usernameRows == 0) {
                    try (PreparedStatement createUserPs = con.prepareStatement(
                            "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)"
                    )) {
                        createUserPs.setInt(1, empId);
                        createUserPs.setString(2, updatedUsername);
                        createUserPs.setString(3, "changeme");
                        createUserPs.setString(4, "employee");
                        createUserPs.executeUpdate();
                    }
                }

                String newPassword = new String(passwordField.getPassword()).trim();
                if (!newPassword.isEmpty()) {
                    PreparedStatement updateUserPs = con.prepareStatement(
                            "UPDATE users SET password = ? WHERE id = ? AND role = ?"
                    );
                    updateUserPs.setString(1, newPassword);
                    updateUserPs.setInt(2, empId);
                    updateUserPs.setString(3, "employee");
                    int userRows = updateUserPs.executeUpdate();

                    if (userRows == 0) {
                        try (PreparedStatement insertUserPs = con.prepareStatement(
                                "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)"
                        )) {
                            insertUserPs.setInt(1, empId);
                            insertUserPs.setString(2, updatedUsername);
                            insertUserPs.setString(3, newPassword);
                            insertUserPs.setString(4, "employee");
                            insertUserPs.executeUpdate();
                        }
                    }
                }

                con.commit();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Employee updated successfully");
                    showEmployees();
                } else {
                    JOptionPane.showMessageDialog(frame, "No changes were made");
                }
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error editing employee: " + e.getMessage());
        }
    }

    private void addDepartment() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField headField = new JTextField();

        panel.add(new JLabel("Department ID:"));
        panel.add(idField);
        panel.add(new JLabel("Department Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Department Head:"));
        panel.add(headField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Department", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Department ID and Name are required");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Department (department_id, d_name, d_head) VALUES (?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            ps.setString(2, nameField.getText().trim());
            ps.setString(3, headField.getText().trim().isEmpty() ? null : headField.getText().trim());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Department added successfully");
            showDepartments();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Department ID must be a number");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding department: " + e.getMessage());
        }
    }

    private void editDepartment(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a department");
            return;
        }

        int deptId = (int) model.getValueAt(row, 0);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement fetchPs = con.prepareStatement(
                     "SELECT d_name, d_head FROM Department WHERE department_id = ?")) {
            fetchPs.setInt(1, deptId);
            ResultSet rs = fetchPs.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(frame, "Department not found");
                return;
            }

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            JTextField nameField = new JTextField(rs.getString("d_name"));
            JTextField headField = new JTextField(rs.getString("d_head") == null ? "" : rs.getString("d_head"));

            panel.add(new JLabel("Department Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Department Head:"));
            panel.add(headField);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Department #" + deptId, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            try (PreparedStatement updatePs = con.prepareStatement(
                    "UPDATE Department SET d_name = ?, d_head = ? WHERE department_id = ?")) {
                updatePs.setString(1, nameField.getText().trim());
                updatePs.setString(2, headField.getText().trim().isEmpty() ? null : headField.getText().trim());
                updatePs.setInt(3, deptId);
                updatePs.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame, "Department updated successfully");
            showDepartments();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating department: " + e.getMessage());
        }
    }

    private void removeDepartment(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a department");
            return;
        }

        int deptId = (int) model.getValueAt(row, 0);

        int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to remove department " + deptId + "?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION
        );
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            PreparedStatement clearEmployees = con.prepareStatement(
                    "UPDATE Employee SET department_id = NULL WHERE department_id = ?"
            );
            clearEmployees.setInt(1, deptId);
            clearEmployees.executeUpdate();

            PreparedStatement clearJobRoles = con.prepareStatement(
                    "UPDATE Job_Role SET dept_id = NULL WHERE dept_id = ?"
            );
            clearJobRoles.setInt(1, deptId);
            clearJobRoles.executeUpdate();

            PreparedStatement clearProjects = con.prepareStatement(
                    "UPDATE Projects SET dept_id = NULL WHERE dept_id = ?"
            );
            clearProjects.setInt(1, deptId);
            clearProjects.executeUpdate();

            PreparedStatement clearMeetings = con.prepareStatement(
                    "UPDATE Meeting SET dept_id = NULL WHERE dept_id = ?"
            );
            clearMeetings.setInt(1, deptId);
            clearMeetings.executeUpdate();

            PreparedStatement deleteBranchLinks = con.prepareStatement(
                    "DELETE FROM Branch_Dept WHERE dept_id = ?"
            );
            deleteBranchLinks.setInt(1, deptId);
            deleteBranchLinks.executeUpdate();

            PreparedStatement deleteDepartment = con.prepareStatement(
                    "DELETE FROM Department WHERE department_id = ?"
            );
            deleteDepartment.setInt(1, deptId);
            int rows = deleteDepartment.executeUpdate();

            con.commit();
            con.setAutoCommit(true);

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Department removed successfully");
                showDepartments();
            } else {
                JOptionPane.showMessageDialog(frame, "Department not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing department: " + e.getMessage());
        }
    }

    private void addRole() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        JTextField idField = new JTextField();
        JTextField designationField = new JTextField();
        JTextField hoursField = new JTextField();
        JTextField baseSalaryField = new JTextField();
        JTextField maxBonusField = new JTextField();
        JTextField minExpField = new JTextField();
        JTextField jobTypeField = new JTextField();
        JTextField totalLeavesField = new JTextField();
        JTextField deptIdField = new JTextField();

        panel.add(new JLabel("Role ID:"));
        panel.add(idField);
        panel.add(new JLabel("Designation:"));
        panel.add(designationField);
        panel.add(new JLabel("Work Hours:"));
        panel.add(hoursField);
        panel.add(new JLabel("Base Salary:"));
        panel.add(baseSalaryField);
        panel.add(new JLabel("Max Bonus:"));
        panel.add(maxBonusField);
        panel.add(new JLabel("Min Experience:"));
        panel.add(minExpField);
        panel.add(new JLabel("Job Type:"));
        panel.add(jobTypeField);
        panel.add(new JLabel("Total Leaves:"));
        panel.add(totalLeavesField);
        panel.add(new JLabel("Department ID (optional):"));
        panel.add(deptIdField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Role", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (idField.getText().trim().isEmpty() || designationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Role ID and Designation are required");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Job_Role (role_id, designation, work_hours, base_salary, max_bonus, min_exp, job_type, total_leaves, dept_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            ps.setString(2, designationField.getText().trim());
            ps.setInt(3, hoursField.getText().trim().isEmpty() ? 0 : Integer.parseInt(hoursField.getText().trim()));
            ps.setDouble(4, baseSalaryField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(baseSalaryField.getText().trim()));
            ps.setDouble(5, maxBonusField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(maxBonusField.getText().trim()));
            ps.setInt(6, minExpField.getText().trim().isEmpty() ? 0 : Integer.parseInt(minExpField.getText().trim()));
            ps.setString(7, jobTypeField.getText().trim());
            ps.setInt(8, totalLeavesField.getText().trim().isEmpty() ? 0 : Integer.parseInt(totalLeavesField.getText().trim()));
            if (deptIdField.getText().trim().isEmpty()) {
                ps.setNull(9, java.sql.Types.INTEGER);
            } else {
                ps.setInt(9, Integer.parseInt(deptIdField.getText().trim()));
            }

            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Role added successfully");
            showRoles();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for numeric fields");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding role: " + e.getMessage());
        }
    }

    private void editRole(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a role");
            return;
        }

        int roleId = (int) model.getValueAt(row, 0);

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement fetchPs = con.prepareStatement(
                    "SELECT designation, work_hours, base_salary, max_bonus, min_exp, job_type, total_leaves, dept_id FROM Job_Role WHERE role_id = ?"
            );
            fetchPs.setInt(1, roleId);
            ResultSet rs = fetchPs.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(frame, "Role not found");
                return;
            }

            JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
            JTextField designationField = new JTextField(rs.getString("designation"));
            JTextField hoursField = new JTextField(String.valueOf(rs.getInt("work_hours")));
            JTextField baseSalaryField = new JTextField(String.valueOf(rs.getDouble("base_salary")));
            JTextField maxBonusField = new JTextField(String.valueOf(rs.getDouble("max_bonus")));
            JTextField minExpField = new JTextField(String.valueOf(rs.getInt("min_exp")));
            JTextField jobTypeField = new JTextField(rs.getString("job_type"));
            JTextField totalLeavesField = new JTextField(String.valueOf(rs.getInt("total_leaves")));
            JTextField deptIdField = new JTextField(rs.getObject("dept_id") == null ? "" : String.valueOf(rs.getInt("dept_id")));

            panel.add(new JLabel("Designation:"));
            panel.add(designationField);
            panel.add(new JLabel("Work Hours:"));
            panel.add(hoursField);
            panel.add(new JLabel("Base Salary:"));
            panel.add(baseSalaryField);
            panel.add(new JLabel("Max Bonus:"));
            panel.add(maxBonusField);
            panel.add(new JLabel("Min Experience:"));
            panel.add(minExpField);
            panel.add(new JLabel("Job Type:"));
            panel.add(jobTypeField);
            panel.add(new JLabel("Total Leaves:"));
            panel.add(totalLeavesField);
            panel.add(new JLabel("Department ID (optional):"));
            panel.add(deptIdField);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Role #" + roleId, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            PreparedStatement updatePs = con.prepareStatement(
                    "UPDATE Job_Role SET designation = ?, work_hours = ?, base_salary = ?, max_bonus = ?, min_exp = ?, job_type = ?, total_leaves = ?, dept_id = ? WHERE role_id = ?"
            );
            updatePs.setString(1, designationField.getText().trim());
            updatePs.setInt(2, hoursField.getText().trim().isEmpty() ? 0 : Integer.parseInt(hoursField.getText().trim()));
            updatePs.setDouble(3, baseSalaryField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(baseSalaryField.getText().trim()));
            updatePs.setDouble(4, maxBonusField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(maxBonusField.getText().trim()));
            updatePs.setInt(5, minExpField.getText().trim().isEmpty() ? 0 : Integer.parseInt(minExpField.getText().trim()));
            updatePs.setString(6, jobTypeField.getText().trim());
            updatePs.setInt(7, totalLeavesField.getText().trim().isEmpty() ? 0 : Integer.parseInt(totalLeavesField.getText().trim()));
            if (deptIdField.getText().trim().isEmpty()) {
                updatePs.setNull(8, java.sql.Types.INTEGER);
            } else {
                updatePs.setInt(8, Integer.parseInt(deptIdField.getText().trim()));
            }
            updatePs.setInt(9, roleId);

            int rows = updatePs.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Role updated successfully");
                showRoles();
            } else {
                JOptionPane.showMessageDialog(frame, "No changes were made");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for numeric fields");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error editing role: " + e.getMessage());
        }
    }

    private void removeRole(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a role");
            return;
        }

        int roleId = (int) model.getValueAt(row, 0);

        int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to remove role " + roleId + "?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION
        );
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            PreparedStatement clearEmployees = con.prepareStatement(
                    "UPDATE Employee SET role_id = NULL WHERE role_id = ?"
            );
            clearEmployees.setInt(1, roleId);
            clearEmployees.executeUpdate();

            PreparedStatement clearRecruitment = con.prepareStatement(
                    "UPDATE Recruitment SET role_id = NULL WHERE role_id = ?"
            );
            clearRecruitment.setInt(1, roleId);
            clearRecruitment.executeUpdate();

            PreparedStatement deleteRoleSkills = con.prepareStatement(
                    "DELETE FROM Role_Skills WHERE role_id = ?"
            );
            deleteRoleSkills.setInt(1, roleId);
            deleteRoleSkills.executeUpdate();

            PreparedStatement deleteRole = con.prepareStatement(
                    "DELETE FROM Job_Role WHERE role_id = ?"
            );
            deleteRole.setInt(1, roleId);
            int rows = deleteRole.executeUpdate();

            con.commit();
            con.setAutoCommit(true);

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Role removed successfully");
                showRoles();
            } else {
                JOptionPane.showMessageDialog(frame, "Role not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing role: " + e.getMessage());
        }
    }

    private JPanel createSectionHeader(String title, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.WEST);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setGridColor(new Color(189, 195, 199));
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setSelectionForeground(new Color(44, 62, 80));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.getTableHeader().setForeground(new Color(44, 62, 80));
    }

    private void styleActionButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void logout() {
        frame.dispose();
        new LoginUI();
    }

    @Override
    public void showDashboard() {
        frame.setVisible(true);
        showSummary();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void validatePhoneNumber(String phoneNumber, String fieldLabel) throws PhoneNumberValidationException {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new PhoneNumberValidationException(fieldLabel + " must be exactly 10 digits");
        }
    }
}

