package application;

import java.util.ArrayList;
import java.util.List;

import middleware.annotations.InstanceScope;
import middleware.annotations.RemoteComponent;
import middleware.annotations.RemoteMethod;
import middleware.annotations.RemoteMethod.RequestType;
import middleware.annotations.InstanceScope.ScopeType;

@InstanceScope(ScopeType.STATIC)
@RemoteComponent("logger")
public class Logger {
    List<Log> logs;

    public Logger() {
        logs = new ArrayList<>();
    }

    public void addLog(Log log) {
        if (log == null) {
            throw new IllegalArgumentException("Log message cannot be null.");
        }
        logs.add(log);
    }

    @RemoteMethod(name = "all", requestType = RequestType.GET)
    public List<Log> getLogs() {
        return logs;
    }

    @RemoteMethod(name = "clear", requestType = RequestType.POST)
    public String clearLogs() {
        logs.clear();
        return "Logs cleared successfully.";
    }
}
