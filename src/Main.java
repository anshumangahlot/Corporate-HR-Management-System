
import java.sql.*;

import java.sql.Connection;

import db.DBConnection;

public class Main {
    public static void main(String[] args) {
        Connection con = DBConnection.getConnection();

        try {
            // INSERT
            // DML-Insert
            String insert = "INSERT INTO Employee VALUES (1, 'Anshuman', 'ansh@gmail.com')";
            Statement st = con.createStatement();
            st.executeUpdate(insert);
            System.out.println("Inserted!");

            // SELECT
            // DRL-Select
            String select = "SELECT * FROM Employee";
            ResultSet rs = st.executeQuery(select);

            while (rs.next()) {
                System.out.println(
                    rs.getInt(1) + " " +
                    rs.getString(2) + " " +
                    rs.getString(3)
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
