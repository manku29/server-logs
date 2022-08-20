/**
 * 
 */
package com.md.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.md.model.Event;

/**
 * @author md
 *
 */
public class EventProcessCalculation implements Runnable {

	public static final Logger logger = LoggerFactory.getLogger(EventProcessCalculation.class);
	private ConcurrentMap<String,ChildLogEvent > map = null;

	private String line = "";

   private static final String insertEvent = "INSERT INTO EVENT " +
		        "  (id, host, type, duration, alert) VALUES " +
		        " (?, ?, ?, ?, ?);";
	   
		
	/**
	 * @param map
	 * @param line
	 * @param session
	 */
	public EventProcessCalculation(ConcurrentMap<String, ChildLogEvent> map, String line) {
		super();
		this.map = map;
		this.line = line;

	}

;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("start the process .... ");
		this.process(map,line);
		logger.info("end the process for thread  ");
	}

	/**
	 *  proess event based on input 
	 */
	private  void process(ConcurrentMap<String,ChildLogEvent> map, String line) {
		ChildLogEvent logEvent = new Gson().fromJson(line, ChildLogEvent.class);
		ChildLogEvent previus = null ;
		boolean calculate = false; 
		if(map.containsKey(logEvent.getId())) {
			previus = map.get(logEvent.getId());
			calculate = true; 
		}else {
			previus = map.putIfAbsent(logEvent.getId(),logEvent);
		}
    	
		if(calculate) {
    		Event event = getEventFromLogs(previus,logEvent);
    		logger.debug(event.toString());
	    //	session.persist(event);
	    	System.out.println("calculated for ID " + event.getId() +" time duration:  "
	    			+ " " +event.getDuration() +" Event falg : "+event.isAlert());
	    	//map.remove(previus.getId());
	    	insertRecord(event);
    	}	    				    	
    }
	
	/**
	 * event verify and compare
	 * @param eventOne
	 * @param eventTwo
	 * @return
	 */
	private static Event getEventFromLogs(ChildLogEvent eventOne, ChildLogEvent eventTwo) {
		Event event = new Event();
		event.setId(eventOne.getId());
		event.setDuration(calculateTime(eventOne.getTimestamp(),eventTwo.getTimestamp()));
		event.setHost(eventOne.getHost());
		event.setType(eventOne.getType());
		event.setAlert(event.getDuration()>4000);
		return event;
	}
	
	private static long calculateTime(long l1, long l2) {
		return l1 > l2 ?  l1-l2 : l2-l1;
	}
	
	
	/*
	 * save in DB
	 */
	public void insertRecord(Event event)  {
        System.out.println(insertEvent);
        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(insertEvent)) {
            preparedStatement.setString(1, event.getId());
            preparedStatement.setString(2, event.getHost());
            preparedStatement.setString(3, event.getType());
            preparedStatement.setString(4, String.valueOf(event.getDuration()));
            preparedStatement.setString(5, String.valueOf(event.isAlert()));

            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
        	logger.error("Exception while saving in DB for event :{}",event);
            
        }
}
}
