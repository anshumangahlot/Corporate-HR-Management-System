package db;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            String url = "jdbc:mysql://localhost:3306/hr_database"; 
            String user = "root";
            String password = "@Gardevoir7"; 

            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected successfully!");
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
        return con;
    }
}