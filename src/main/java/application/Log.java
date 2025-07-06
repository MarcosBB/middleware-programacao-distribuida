package application;

public class Log {
    private String msg;
    private LogLevel level;
    private String timestamp;

    public Log(String msg, LogLevel level, String timestamp) {
        if (msg == null || msg.isEmpty()) {
            throw new IllegalArgumentException("Log message cannot be null or empty.");
        }
        if (level == null) {
            throw new IllegalArgumentException("Log level cannot be null.");
        }
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("Timestamp cannot be null or empty.");
        }
        this.msg = msg;
        this.level = level;
        this.timestamp = timestamp;
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
