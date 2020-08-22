package sase.input.producers;

import sase.specification.SimulationSpecification;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class SensorPreprocessedFileBasedEventProducer extends FileBasedEventProducer {
    private final LocalDateTime initialDateTime = LocalDateTime.of(2020, 8, 22, 0 , 0, 0);
    private LocalDateTime lastDateTimeObserved = initialDateTime;
    private int weeksPassed = 0;
    private boolean isFirstLine = true;

    private final int hoursIndex = 0;
    private final int secondsIndex = 1;
    private final int dayOfWeekIndex = 2;

    public SensorPreprocessedFileBasedEventProducer(SimulationSpecification simulationSpecification) {
        super(simulationSpecification);
    }

    protected String[] preprocessLineBeforeProducingEvent(String[] rawEvent) {
        if (isFirstLine) {
            isFirstLine = false;
            return rawEvent;
        }
        String[] preprocessedEvent = new String[rawEvent.length - 2]; //Merge 3 integers(hour,second,week) to one timestamp
        preprocessedEvent[0] = convertToTimestamp(
                Double.valueOf(rawEvent[hoursIndex]).intValue(),
                Double.valueOf(rawEvent[secondsIndex]).intValue(),
                Double.valueOf(rawEvent[dayOfWeekIndex]).intValue()).toString();
        for (int i = 1; i < preprocessedEvent.length; i++) {
            preprocessedEvent[i] = rawEvent[i + 2];
        }
        return preprocessedEvent;
    }

    private LocalDateTime convertToTimestamp(int hours, int seconds, int dayOfWeek) {
        LocalDateTime currentDateTime = initialDateTime.plusSeconds(seconds).plusDays(dayOfWeek).plusWeeks(weeksPassed);
        if (currentDateTime.isBefore(lastDateTimeObserved)) {
            currentDateTime = currentDateTime.plusWeeks(1);
            weeksPassed++;
        }
        lastDateTimeObserved = currentDateTime;
//        return Timestamp.valueOf(lastDateTimeObserved).getTime();
        return lastDateTimeObserved;
    }
}
