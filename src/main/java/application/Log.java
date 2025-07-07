package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private String msg;
    private LogLevel level;
    private String timestamp;

    public Log(String msg, LogLevel level) {
        if (msg == null || msg.isEmpty()) {
            throw new IllegalArgumentException("Log message cannot be null or empty.");
        }
        if (level == null) {
            throw new IllegalArgumentException("Log level cannot be null.");
        }
        this.msg = msg;
        this.level = level;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss.SSS"));
    }

    public String getMsg() {
        return msg;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
