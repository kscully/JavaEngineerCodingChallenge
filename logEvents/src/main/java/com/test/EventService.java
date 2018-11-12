package com.test;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    Scanner sc = null;
    Map<String, LogEvent> events = new HashMap<>();
    DBEventDAO dbEventDao = new DBEventDAO();

    ExecutorService executor = Executors.newFixedThreadPool(5);


    public static void main(String[] args) {
        logger.info("Starting event service.");
        EventService eventService = new EventService();
        eventService.processEvents("/events");
        logger.info("Event service complete.");
    }

    public void processEvents(String inputFile) {
        try {
            logger.debug("Reading input file.");
            openFile(inputFile);
            logger.debug("Persisting events");
            persistEvents();
            logger.debug("Closing file and thread executor.");
            closeFile();
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            logger.debug("Finished all event processing");

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void openFile(String path) throws IOException {
        InputStream in = this.getClass().getResourceAsStream(path);
        sc = new Scanner(in);
    }


    public void closeFile() {
        sc.close();
    }

    public void persistEvents() {
        while (sc.hasNextLine()) {
            LogEvent currentEvent = getEvent(sc.nextLine());
            // check if that id has been recorded already
            if (events.containsKey(currentEvent.getId())) {
                logger.debug("Found matching event: " + currentEvent.getId());
                Runnable eventProcessor = new EventProcessor(currentEvent, events, dbEventDao);
                logger.debug("Running event processing.");
                executor.execute(eventProcessor);
            } else {
                events.put(currentEvent.getId(), currentEvent);
            }
        }
    }

    public LogEvent getEvent(String eventString) {
        logger.debug("Retrieving event log object.");
        JSONObject eventJson = new JSONObject(eventString);

        String id = eventJson.getString("id");
        String state = eventJson.getString("state");
        String type = eventJson.has("type") ? eventJson.getString("type") : null;
        String host = eventJson.has("host") ? eventJson.getString("host") : null;
        Long timestamp = eventJson.getLong("timestamp");

        LogEvent logEvent = new LogEvent(id, state, type, host, timestamp);

        logger.debug("Done.");

        return logEvent;
    }


}
