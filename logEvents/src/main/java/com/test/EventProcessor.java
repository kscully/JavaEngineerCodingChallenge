package com.test;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EventProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);


    LogEvent currentEvent;
    Map<String, LogEvent> events;
    DBEventDAO dbEventDao;

    public EventProcessor(LogEvent currentLogEvent, Map<String, LogEvent> events, DBEventDAO dbEventDao) {
        this.currentEvent = currentLogEvent;
        this.events = events;
        this.dbEventDao = dbEventDao;
    }

    @Override
    public void run() {
        // get the first occurrence of the db event
        LogEvent firstEvent = events.get(currentEvent.getId());
        // normalise both log events to a DB event
        DBEvent dbEvent = getDBEvent(firstEvent, currentEvent);
        // save the DB event
        dbEventDao.saveDBEvent(dbEvent);

        // remove the event from the map, if we don't do this we will get an out of memory with a very large file
        events.remove(currentEvent.getId());
    }

    public DBEvent getDBEvent(LogEvent logEvent1, LogEvent logEvent2) {
        logger.debug("Calculating event durations, id is: " + logEvent1.getId());
        LogEvent startLogEvent = null;
        LogEvent finishLogEvent = null;
        // figure out which event is the start event as events have no specific order.
        if (logEvent1.isStartEvent()) {
            startLogEvent = logEvent1;
            finishLogEvent = logEvent2;
        } else {
            // otherwise we are dealing with a finish event so flip them around.
            startLogEvent = logEvent2;
            finishLogEvent = logEvent1;
        }

        logger.debug("Start event time is: " + startLogEvent.getTimestamp());
        logger.debug("End event time is: " + finishLogEvent.getTimestamp());

        Long duration = finishLogEvent.getTimestamp() - startLogEvent.getTimestamp();
        logger.debug("Duration is: " + duration);

        Boolean alert = duration > 4;

        return new DBEvent(finishLogEvent.getId(), duration, finishLogEvent.getType(), finishLogEvent.getHost(), alert);

    }

    public LogEvent getEvent(String eventString) {
        JSONObject eventJson = new JSONObject(eventString);

        String id = eventJson.getString("id");
        String state = eventJson.getString("state");
        String type = eventJson.has("type") ? eventJson.getString("type") : null;
        String host = eventJson.has("host") ? eventJson.getString("host") : null;
        Long timestamp = eventJson.getLong("timestamp");

        LogEvent logEvent = new LogEvent(id, state, type, host, timestamp);

        return logEvent;
    }
}
