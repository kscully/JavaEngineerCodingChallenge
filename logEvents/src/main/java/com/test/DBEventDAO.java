package com.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DBEventDAO {

    private static final Logger logger = LoggerFactory.getLogger(DBEventDAO.class);

    Connection connection;

    public DBEventDAO() {
//        try {
//            connection = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public void saveDBEvent(DBEvent dbEvent) {
        // would use a prepared statement here and just map in the fields from the db event the table
        logger.info("persisting db event " + dbEvent.getId() + ", duration is " + dbEvent.getDuration());
    }

}
