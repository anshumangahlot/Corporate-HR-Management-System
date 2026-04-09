import db.DatabaseOperations;
import exceptions.DatabaseException;
import java.sql.Date;

/**
 * Database Operations Demo
 * Demonstrates all DML and DRL operations required for CO4
 * - INSERT operations
 * - UPDATE operations
 * - DELETE operations
 * - SELECT with various clauses (WHERE, JOIN, GROUP BY, ORDER BY)
 * - Aggregate functions (COUNT)
 */
public class OperationsDemo {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  HR MANAGEMENT SYSTEM - DATABASE OPERATIONS DEMO");
        System.out.println("  Demonstrating all DML and DRL operations");
        System.out.println("========================================");

        try {
            // ========== DML OPERATIONS ==========
            System.out.println("\n\n### DML OPERATIONS (INSERT, UPDATE, DELETE) ###\n");

            // INSERT Operations
            System.out.println("--- INSERT OPERATIONS ---");
            
            System.out.println("\n1. Inserting new user...");
            boolean userInserted = DatabaseOperations.insertUser("john_dev", "pass123", "admin");
            System.out.println("User inserted: " + userInserted);

            System.out.println("\n2. Inserting another user...");
            boolean userInserted2 = DatabaseOperations.insertUser("emma_emp", "secure456", "employee");
            System.out.println("User inserted: " + userInserted2);

            System.out.println("\n3. Inserting new employee...");
            boolean empInserted = DatabaseOperations.insertEmployee(
                1001, 
                "John Developer", 
                "Male", 
                Date.valueOf("1995-05-15"), 
                "john@company.com", 
                1, 
                1
            );
            System.out.println("Employee inserted: " + empInserted);

            // UPDATE Operations
            System.out.println("\n\n--- UPDATE OPERATIONS ---");
            
            System.out.println("\n4. Updating user password...");
            boolean passwordUpdated = DatabaseOperations.updateUserPassword(1, "newSecurePass789");
            System.out.println("Password updated: " + passwordUpdated);

            System.out.println("\n5. Updating employee information...");
            boolean empUpdated = DatabaseOperations.updateEmployeeInfo(1001, "john.new@company.com", "123 New Street");
            System.out.println("Employee info updated: " + empUpdated);

            // DELETE Operations
            System.out.println("\n\n--- DELETE OPERATIONS ---");
            
            System.out.println("\n6. Deleting a user...");
            boolean userDeleted = DatabaseOperations.deleteUser(4);
            System.out.println("User deleted: " + userDeleted);

            System.out.println("\n7. Deleting an employee...");
            boolean empDeleted = DatabaseOperations.deleteEmployee(2);
            System.out.println("Employee deleted: " + empDeleted);

            // ========== DRL OPERATIONS ==========
            System.out.println("\n\n### DRL OPERATIONS (SELECT) ###\n");

            // Simple SELECT
            System.out.println("--- SIMPLE SELECT ---");
            DatabaseOperations.selectAllUsers();

            // SELECT with WHERE clause
            System.out.println("\n\n--- SELECT WITH WHERE CLAUSE ---");
            DatabaseOperations.selectUsersByRole("admin");
            DatabaseOperations.selectUsersByRole("employee");

            // SELECT with specific ID
            System.out.println("\n\n--- SELECT EMPLOYEE BY ID ---");
            DatabaseOperations.selectEmployeeById(1);
            DatabaseOperations.selectEmployeeById(1001);

            // SELECT with JOIN
            System.out.println("\n\n--- SELECT WITH INNER JOIN ---");
            DatabaseOperations.selectEmployeesWithDepartment();

            // SELECT with COUNT aggregate
            System.out.println("\n\n--- SELECT WITH AGGREGATE FUNCTIONS ---");
            DatabaseOperations.getTotalUsers();
            DatabaseOperations.getTotalEmployees();

            // SELECT with GROUP BY
            System.out.println("\n\n--- SELECT WITH GROUP BY CLAUSE ---");
            DatabaseOperations.getEmployeeCountByDepartment();

            // SELECT with ORDER BY
            System.out.println("\n\n--- SELECT WITH ORDER BY CLAUSE ---");
            DatabaseOperations.getEmployeesOrderedByName();

        } catch (DatabaseException e) {
            System.out.println("\nDatabase Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("\nUnexpected Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n\n========================================");
        System.out.println("  DATABASE OPERATIONS DEMO COMPLETED");
        System.out.println("========================================\n");
    }
}
