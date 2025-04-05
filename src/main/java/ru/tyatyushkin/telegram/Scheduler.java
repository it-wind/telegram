package ru.tyatyushkin.telegram;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private final static ZoneId zoneId = ZoneId.of("Europe/Moscow");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public void addTaskAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit type) {
        scheduler.scheduleAtFixedRate(task, initialDelay, period, type);
    }

    public void addTaskDaily(Runnable task, int hours, int minutes) {
        long initialDelay = calculateInitialDelay(hours,minutes);
        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    private static long calculateInitialDelay(int hours, int minutes) {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = now.withHour(hours).withMinute(minutes).withSecond(0).withNano(0);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return ChronoUnit.MILLIS.between(now, nextRun);
    }
}
