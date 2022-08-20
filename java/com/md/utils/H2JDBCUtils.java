/**
 * 
 */
package com.md.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author md
 *
 */
public class H2JDBCUtils {

	 private static String jdbcURL = "jdbc:hsqldb:file:testdb";
	    private static String jdbcUsername = "sa";
	    private static String jdbcPassword = "";

	    public static Connection getConnection() {
	        Connection connection = null;
	        try {
	            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
	        } catch (SQLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return connection;
	    }

	    public static void printSQLException(SQLException ex) {
	        for (Throwable e: ex) {
	            if (e instanceof SQLException) {
	                e.printStackTrace(System.err);
	                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
	                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
	                System.err.println("Message: " + e.getMessage());
	                Throwable t = ex.getCause();
	                while (t != null) {
	                    System.out.println("Cause: " + t);
	                    t = t.getCause();
	                }
	            }
	        }
	    }
	}
