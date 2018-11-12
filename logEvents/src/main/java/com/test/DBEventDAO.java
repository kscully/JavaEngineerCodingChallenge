package com.test;

import java.sql.Connection;

public class DBEventDAO {

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
        System.out.println("persisting db event " + dbEvent.getId() + ", duration is " + dbEvent.getDuration());
    }

}
