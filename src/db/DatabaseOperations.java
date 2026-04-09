package db;

import exceptions.DatabaseException;
import java.sql.*;

/**
 * Database Operations Class
 * Demonstrates comprehensive DML and DRL operations (CO4)
 * Includes INSERT, UPDATE, DELETE, SELECT with JOINs and aggregations
 */
public class DatabaseOperations {

    // ============ INSERT OPERATIONS (DML) ============

    /**
     * INSERT - Add a new user to the database
     * Demonstrates DML INSERT operation
     */
    public static boolean insertUser(String username, String password, String role) 
            throws DatabaseException {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert user: " + e.getMessage(), e);
        }
    }

    /**
     * INSERT - Add a new employee to the database
     */
    public static boolean insertEmployee(int empID, String empName, String gender, 
                                        Date dob, String email, int deptId, int roleId) 
            throws DatabaseException {
        String query = "INSERT INTO Employee (EmpID, Emp_name, Gender, DOB, Email, department_id, role_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, empID);
            ps.setString(2, empName);
            ps.setString(3, gender);
            ps.setDate(4, dob);
            ps.setString(5, email);
            ps.setInt(6, deptId);
            ps.setInt(7, roleId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert employee: " + e.getMessage(), e);
        }
    }

    // ============ UPDATE OPERATIONS (DML) ============

    /**
     * UPDATE - Modify user password
     * Demonstrates DML UPDATE operation
     */
    public static boolean updateUserPassword(int userId, String newPassword) 
            throws DatabaseException {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update password: " + e.getMessage(), e);
        }
    }

    /**
     * UPDATE - Modify employee information
     */
    public static boolean updateEmployeeInfo(int empID, String email, String street) 
            throws DatabaseException {
        String query = "UPDATE Employee SET Email = ?, Street = ? WHERE EmpID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, email);
            ps.setString(2, street);
            ps.setInt(3, empID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update employee info: " + e.getMessage(), e);
        }
    }

    // ============ DELETE OPERATIONS (DML) ============

    /**
     * DELETE - Remove a user from the database
     * Demonstrates DML DELETE operation
     */
    public static boolean deleteUser(int userId) 
            throws DatabaseException {
        String query = "DELETE FROM users WHERE id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, userId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    /**
     * DELETE - Remove an employee from the database
     */
    public static boolean deleteEmployee(int empID) 
            throws DatabaseException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement deleteSalaryBreakdownPs = con.prepareStatement(
                        "DELETE FROM Salary_Breakdown WHERE payroll_id IN (SELECT payroll_id FROM Payroll WHERE EmpID = ?)"
                )) {
                    deleteSalaryBreakdownPs.setInt(1, empID);
                    deleteSalaryBreakdownPs.executeUpdate();
                }

                try (PreparedStatement deletePayrollPs = con.prepareStatement(
                        "DELETE FROM Payroll WHERE EmpID = ?"
                )) {
                    deletePayrollPs.setInt(1, empID);
                    deletePayrollPs.executeUpdate();
                }

                try (PreparedStatement deleteEmployeeProjectLinksPs = con.prepareStatement(
                        "DELETE FROM Employee_Projects WHERE EmpID = ? OR project_id IN (SELECT project_id FROM Projects WHERE TeamLead = ?)"
                )) {
                    deleteEmployeeProjectLinksPs.setInt(1, empID);
                    deleteEmployeeProjectLinksPs.setInt(2, empID);
                    deleteEmployeeProjectLinksPs.executeUpdate();
                }

                try (PreparedStatement deleteProjectsPs = con.prepareStatement(
                        "DELETE FROM Projects WHERE TeamLead = ?"
                )) {
                    deleteProjectsPs.setInt(1, empID);
                    deleteProjectsPs.executeUpdate();
                }

                try (PreparedStatement deleteAttendancePs = con.prepareStatement(
                        "DELETE FROM Attendance_Log WHERE EmpID = ?"
                )) {
                    deleteAttendancePs.setInt(1, empID);
                    deleteAttendancePs.executeUpdate();
                }

                try (PreparedStatement deleteSickLeavePs = con.prepareStatement(
                        "DELETE FROM Sick_Leave WHERE leave_id IN (SELECT leave_id FROM Leave_Request WHERE EmpID = ?)"
                )) {
                    deleteSickLeavePs.setInt(1, empID);
                    deleteSickLeavePs.executeUpdate();
                }

                try (PreparedStatement deleteCasualLeavePs = con.prepareStatement(
                        "DELETE FROM Casual_Leave WHERE leave_id IN (SELECT leave_id FROM Leave_Request WHERE EmpID = ?)"
                )) {
                    deleteCasualLeavePs.setInt(1, empID);
                    deleteCasualLeavePs.executeUpdate();
                }

                try (PreparedStatement deletePaidLeavePs = con.prepareStatement(
                        "DELETE FROM Paid_Leave WHERE leave_id IN (SELECT leave_id FROM Leave_Request WHERE EmpID = ?)"
                )) {
                    deletePaidLeavePs.setInt(1, empID);
                    deletePaidLeavePs.executeUpdate();
                }

                try (PreparedStatement deleteLeavePs = con.prepareStatement(
                        "DELETE FROM Leave_Request WHERE EmpID = ?"
                )) {
                    deleteLeavePs.setInt(1, empID);
                    deleteLeavePs.executeUpdate();
                }

                try (PreparedStatement deleteRecruitmentPs = con.prepareStatement(
                        "DELETE FROM Recruitment WHERE EmpID = ? OR recruiter_id = ?"
                )) {
                    deleteRecruitmentPs.setInt(1, empID);
                    deleteRecruitmentPs.setInt(2, empID);
                    deleteRecruitmentPs.executeUpdate();
                }

                try (PreparedStatement deleteInternPs = con.prepareStatement(
                        "DELETE FROM Intern WHERE EmpID = ?"
                )) {
                    deleteInternPs.setInt(1, empID);
                    deleteInternPs.executeUpdate();
                }

                try (PreparedStatement deleteFullTimePs = con.prepareStatement(
                        "DELETE FROM Full_Time WHERE EmpID = ?"
                )) {
                    deleteFullTimePs.setInt(1, empID);
                    deleteFullTimePs.executeUpdate();
                }

                try (PreparedStatement deleteBranchDeptPs = con.prepareStatement(
                        "DELETE FROM Branch_Dept WHERE branch_id IN (SELECT branch_id FROM Branch WHERE mgr_id = ?)"
                )) {
                    deleteBranchDeptPs.setInt(1, empID);
                    deleteBranchDeptPs.executeUpdate();
                }

                try (PreparedStatement deleteBranchesPs = con.prepareStatement(
                        "DELETE FROM Branch WHERE mgr_id = ?"
                )) {
                    deleteBranchesPs.setInt(1, empID);
                    deleteBranchesPs.executeUpdate();
                }

                try (PreparedStatement deletePhonesPs = con.prepareStatement(
                        "DELETE FROM Employee_Phones WHERE EmpID = ?"
                )) {
                    deletePhonesPs.setInt(1, empID);
                    deletePhonesPs.executeUpdate();
                }

                try (PreparedStatement deleteUserCredsPs = con.prepareStatement(
                        "DELETE FROM user_credentials WHERE EmpID = ?"
                )) {
                    deleteUserCredsPs.setInt(1, empID);
                    deleteUserCredsPs.executeUpdate();
                }

                int rowsAffected;
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM Employee WHERE EmpID = ?")) {
                    ps.setInt(1, empID);
                    rowsAffected = ps.executeUpdate();
                }

                con.commit();
                return rowsAffected > 0;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete employee: " + e.getMessage(), e);
        }
    }

    // ============ SELECT OPERATIONS (DRL) ============

    /**
     * SELECT - Get all users from database
     * Demonstrates DRL SELECT operation
     */
    public static void selectAllUsers() 
            throws DatabaseException {
        String query = "SELECT id, username, role FROM users";
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            System.out.println("\n=== ALL USERS ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                 ", Username: " + rs.getString("username") + 
                                 ", Role: " + rs.getString("role"));
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users: " + e.getMessage(), e);
        }
    }

    /**
     * SELECT - Get all employees with their department information (JOIN)
     * Demonstrates DRL SELECT with JOIN operation
     */
    public static void selectEmployeesWithDepartment() 
            throws DatabaseException {
        String query = "SELECT e.EmpID, e.Emp_name, e.Email, d.d_name " +
                      "FROM Employee e " +
                      "INNER JOIN Department d ON e.department_id = d.department_id";
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            System.out.println("\n=== EMPLOYEES WITH DEPARTMENTS (JOIN) ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("EmpID") + 
                                 ", Name: " + rs.getString("Emp_name") + 
                                 ", Email: " + rs.getString("Email") + 
                                 ", Department: " + rs.getString("d_name"));
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch employees with departments: " + e.getMessage(), e);
        }
    }

    /**
     * SELECT - Get users by role (WHERE clause)
     * Demonstrates DRL SELECT with WHERE condition
     */
    public static void selectUsersByRole(String role) 
            throws DatabaseException {
        String query = "SELECT id, username, role FROM users WHERE role = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, role);
            
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== USERS WITH ROLE: " + role + " ===");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + 
                                     ", Username: " + rs.getString("username"));
                }
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users by role: " + e.getMessage(), e);
        }
    }

    /**
     * SELECT - Get employee by ID (WHERE clause)
     * Demonstrates DRL SELECT with specific condition
     */
    public static void selectEmployeeById(int empID) 
            throws DatabaseException {
        String query = "SELECT EmpID, Emp_name, Gender, Email FROM Employee WHERE EmpID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, empID);
            
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== EMPLOYEE WITH ID: " + empID + " ===");
                if (rs.next()) {
                    System.out.println("ID: " + rs.getInt("EmpID") + 
                                     ", Name: " + rs.getString("Emp_name") + 
                                     ", Gender: " + rs.getString("Gender") + 
                                     ", Email: " + rs.getString("Email"));
                } else {
                    System.out.println("No employee found with ID: " + empID);
                }
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch employee: " + e.getMessage(), e);
        }
    }

    /**
     * SELECT - Count total users (AGGREGATION)
     * Demonstrates DRL SELECT with COUNT aggregate function
     */
    public static int getTotalUsers() 
            throws DatabaseException {
        String query = "SELECT COUNT(*) as total FROM users";
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("\n=== TOTAL USERS: " + total + " ===");
                return total;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get user count: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * SELECT - Count total employees (AGGREGATION)
     */
    public static int getTotalEmployees() 
            throws DatabaseException {
        String query = "SELECT COUNT(*) as total FROM Employee";
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("\n=== TOTAL EMPLOYEES: " + total + " ===");
                return total;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get employee count: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * SELECT - Count employees by department (GROUP BY)
     * Demonstrates DRL SELECT with GROUP BY clause
     */
    public static void getEmployeeCountByDepartment() 
            throws DatabaseException {
        String query = "SELECT d.d_name, COUNT(e.EmpID) as emp_count " +
                      "FROM Employee e " +
                      "INNER JOIN Department d ON e.department_id = d.department_id " +
                      "GROUP BY d.d_name";
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            System.out.println("\n=== EMPLOYEES BY DEPARTMENT (GROUP BY) ===");
            while (rs.next()) {
                System.out.println("Department: " + rs.getString("d_name") + 
                                 ", Count: " + rs.getInt("emp_count"));
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get employee count by department: " + e.getMessage(), e);
        }
    }

    /**
     * SELECT - Get employees ordered by name (ORDER BY)
     * Demonstrates DRL SELECT with ORDER BY clause
     */
    public static void getEmployeesOrderedByName() 
            throws DatabaseException {
        String query = "SELECT EmpID, Emp_name, Email FROM Employee ORDER BY Emp_name ASC";
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            System.out.println("\n=== EMPLOYEES ORDERED BY NAME ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("EmpID") + 
                                 ", Name: " + rs.getString("Emp_name") + 
                                 ", Email: " + rs.getString("Email"));
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch ordered employees: " + e.getMessage(), e);
        }
    }
}
