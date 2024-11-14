package org.pzks.utils;

import java.time.Duration;

public class Time {
    private Duration duration;

    public Time() {}

    public Time(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long milliseconds = duration.toMillisPart();

        StringBuilder timeBuilder = new StringBuilder();

        if (hours > 0) {
            timeBuilder.append(hours).append("h");
        }
        if (minutes > 0) {
            if (!timeBuilder.isEmpty()) {
                timeBuilder.append(" ");
            }
            timeBuilder.append(minutes).append("m");
        }
        if (seconds > 0) {
            if (!timeBuilder.isEmpty()) {
                timeBuilder.append(" ");
            }
            timeBuilder.append(seconds).append("s");
        }
        if (milliseconds > 0) {
            if (!timeBuilder.isEmpty()) {
                timeBuilder.append(" ");
            }
            timeBuilder.append(milliseconds).append("ms");
        }

        return timeBuilder.toString();
    }
}
