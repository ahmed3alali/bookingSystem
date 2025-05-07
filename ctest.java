//DB connection setup
package com.mycompany.bookingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ctest {
  public static Connection getConnection() throws ClassNotFoundException, SQLException {
        // Database connection details
        String hostname = "localhost";
        String sqlInstanceName = "Habeeb\\SQLEXPRESS"; //computer name 
        String sqlDatabase = "HRLC_DB";  //sql server database name
        String sqlUser = "sa";
        String sqlPassword = "1234"; //passwrod sa account
        // Load the SQL Server JDBC driver
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(ctest.class.getName()).log(Level.SEVERE, null, e);
            throw e; // Rethrow exception if driver is not found
        }

        // Connection string for SQL Server
        String connectURL = "jdbc:sqlserver://" + hostname + ":1433" + ";instance=" + sqlInstanceName + ";databaseName=" + sqlDatabase + ";encrypt=false";

        // Return the connection object
        try {
            Connection conn = DriverManager.getConnection(connectURL, sqlUser, sqlPassword);
            System.out.println("Connected to the database successfully!");
            return conn;  // Return the connection object
        } catch (SQLException ex) {
            Logger.getLogger(ctest.class.getName()).log(Level.SEVERE, null, ex);
            throw new SQLException("Unable to establish a connection to the database", ex);
        }
    }

    
    
    
    
    
    
    

        
            public static void main(String[] args) throws ClassNotFoundException, SQLException {
        
        
           Connection conn = null;
        try {
            conn = getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ctest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // SQL query to select all records from the employees table
        String query = "SELECT * FROM Stock"; 

        // Create a statement and execute the query
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        // Process the result set
        while (rs.next()) {
            // Assuming the employees table has columns 'id' and 'name'
            int id = rs.getInt("stock_id");
            String name = rs.getString("date");

            System.out.println("ID: " + id + ", Name: " + name); // Print the results
        }

        // Close the resources
        rs.close();
        stmt.close();
        conn.close();
    }
    
}

