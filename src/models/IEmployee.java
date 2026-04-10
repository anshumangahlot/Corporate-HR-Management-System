package models;

/**
 * Employee Interface
 * Demonstrates interface implementation (CO2)
 * Defines contract for employee-related operations
 */
public interface IEmployee {
    // RBR
    
    /**
     * Get employee information
     * @return String representation of employee details
     */
    String getEmployeeInfo();

    /**
     * Apply for leave
     * @param days - Number of days for leave
     * @return true if leave applied successfully, false otherwise
     */
    boolean applyLeave(int days);

    /**
     * View attendance
     * @return Attendance percentage
     */
    double viewAttendance();

    /**
     * View payroll details
     * @return Payroll information as String
     */
    String viewPayroll();

    /**
     * Get department
     * @return Department name
     */
    String getDepartment();

    /**
     * Get role/designation
     * @return Role name
     */
    String getRole();
}
