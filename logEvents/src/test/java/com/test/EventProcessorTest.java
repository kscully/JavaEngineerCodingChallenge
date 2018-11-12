package com.test;

import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EventProcessorTest {

    @Test
    public void testRunEventProcessor() {

        LogEvent logEvent1 = new LogEvent("scsmbstgra", "FINISHED", "APPLICATION_LOG", "12345", 1491377495217L);
        LogEvent currentLogEvent = new LogEvent("scsmbstgra", "STARTED", "APPLICATION_LOG", "12345", 1491377495212L);

        HashMap<String, LogEvent> events = new HashMap<>();
        events.put("scsmbstgra", logEvent1);

        DBEventDAO dbEventDaoMock = mock(DBEventDAO.class);

        EventProcessor eventProcessor = new EventProcessor(currentLogEvent, events, dbEventDaoMock);
        eventProcessor.run();

        verify(dbEventDaoMock).saveDBEvent(any(DBEvent.class));

        assertEquals(0, events.size());
    }


    @Test
    public void testGetDbEvent() {

        LogEvent logEvent1 = new LogEvent("scsmbstgra", "FINISHED", "APPLICATION_LOG", "12345", 1491377495217L);
        LogEvent logEvent2 = new LogEvent("scsmbstgra", "STARTED", "APPLICATION_LOG", "12345", 1491377495212L);

        EventProcessor eventProcessor = new EventProcessor(null, null, null);

        DBEvent dbEvent = eventProcessor.getDBEvent(logEvent1, logEvent2);

        assertEquals(new Long(5), dbEvent.getDuration());
        assertTrue(dbEvent.getAlert());
    }


}