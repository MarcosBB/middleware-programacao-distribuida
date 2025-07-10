package application;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import middleware.annotations.InstanceScope;
import middleware.annotations.Leasable;
import middleware.annotations.Passivable;
import middleware.annotations.RemoteComponent;
import middleware.annotations.RemoteMethod;
import middleware.annotations.InstanceScope.ScopeType;

@RemoteComponent("history")
@InstanceScope(ScopeType.PER_CLIENT)
@Passivable()
@Leasable(leaseTime = 20000)
public class History {
    private Map<String, HistoryEntry> history;
    private static final long ENTRIES_LIMIT = 100;

    public History() {
        this.history = new ConcurrentHashMap<>();
    }

    @RemoteMethod(name = "listall", requestType = RemoteMethod.RequestType.GET)
    public List<HistoryEntry> listAll() {
        return history.values().stream().toList();
    }

    @RemoteMethod(name = "clear", requestType = RemoteMethod.RequestType.DELETE)
    public String clear() {
        history.clear();
        return "History cleared successfully.";
    }

    public void addEntry(String requestId, Object result, Map<String, Object> context) {
        if (this.history.size() <= ENTRIES_LIMIT) {
            history.put(requestId, new HistoryEntry(result, context));
        }
    }

    public String toString() {
        return history.toString();
    }

    public Map<String, HistoryEntry> getHistory() {
        return history;
    }

    public void setHistory(Map<String, HistoryEntry> history) {
        this.history = history;
    }

}
