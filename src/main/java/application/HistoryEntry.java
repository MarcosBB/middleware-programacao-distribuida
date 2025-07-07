package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryEntry {
    private Object result;
    private Object context;
    private String timeStamp;

    public HistoryEntry(Object result, Object context) {
        this.result = result;
        this.context = context;
        this.timeStamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public Object getResult() {
        return result;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Object getContext() {
        return context;
    }
}
