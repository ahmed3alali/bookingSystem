package com.mycompany.bookingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        // Database connection details
        String hostname = "localhost";
        String sqlInstanceName = "Habeeb\\SQLEXPRESS"; //computer name 
        String sqlDatabase = "flight_booking_system";  //sql server database name
        String sqlUser = "sa";
        String sqlPassword = "1234"; //passwrod sa account
        // Load the SQL Server JDBC driver
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            throw new SQLException("Unable to establish a connection to the database", ex);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Connection conn = null;
        try {
            conn = getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        conn.close();
    }

    /**
     * Close the given connection quietly
     *
     * @param conn Connection to close
     */
    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Close the given AutoCloseable resources quietly
     *
     * @param resources Resources to close
     */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}
