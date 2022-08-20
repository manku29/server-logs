/**
 * 
 */
package com.md.logs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.md.utils.ChildLogEvent;
import com.md.utils.EventProcessCalculation;
import com.md.utils.H2JDBCUtils;

/**
 * @author md
 *
 */
public class ApplicationLogsCalculation {
	public static final Logger logger = LoggerFactory.getLogger(ApplicationLogsCalculation.class);

	static ConcurrentMap<String, ChildLogEvent> map = new ConcurrentHashMap<>();

	private static final String createTableSQL = "create table Event ( id  varchar(10) primary key,  host varchar(20),  type varchar(20), duration String(20), alert varchar(20) );";

	

	public static void main(String... args) {
		ApplicationLogsCalculation lc = new ApplicationLogsCalculation();
		if (!lc.validateInputFile(args)) {
			logger.warn("Incorrect file or path for input file");
			return;
		}
		logger.info("Reading file: " + args[0]);
		lc.readEvents(args[0]);
		logger.info("Execution completed");

	}

	/**
	 * event process start for input file
	 * @param fileName
	 */
	public void readEvents(String fileName) {


		InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			ExecutorService ex = Executors.newFixedThreadPool(4);
			String line;
			while ((line = br.readLine()) != null) {
				logger.info(line);
				ex.execute(new EventProcessCalculation(map, line ));
			}
			ex.shutdown();
		} catch (Exception e) {
			logger.error("File not found Error : {}", e.getMessage());
		}

	}

	
	/**
	 * Verify file 
	 * @param args
	 * @return
	 */
	public boolean validateInputFile(String[] args) {

		if (args != null && args[0] != null && this.getClass().getClassLoader().getResourceAsStream(args[0]) != null) {

			logger.info(" file received file path :[]", args[0]);
			return true;
		}
		logger.error("Path is empty");
		return false;
	}
	
	
//create table when create first time 
	public void createTable() throws SQLException {

        System.out.println(createTableSQL);
        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();
            // Step 2:Create a statement using connection object
            Statement statement = connection.createStatement();) {
            // Step 3: Execute the query or update query
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            // print SQL exception information
            H2JDBCUtils.printSQLException(e);
        }
    }

}
