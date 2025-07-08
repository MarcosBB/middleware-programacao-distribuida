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
@Leasable(leaseTime = 5000)
public class History {
    private Map<String, HistoryEntry> history;

    public History() {
        this.history = new ConcurrentHashMap<>();
    }

    @RemoteMethod(name = "getall", requestType = RemoteMethod.RequestType.GET)
    public List<HistoryEntry> getAll() {
        return history.values().stream().toList();
    }

    @RemoteMethod(name = "clear", requestType = RemoteMethod.RequestType.DELETE)
    public String clear() {
        history.clear();
        return "History cleared successfully.";
    }

    public void addEntry(String requestId, Object result, Object context) {
        history.put(requestId, new HistoryEntry(result, context));
    }

    public String toString() {
        return history.toString();
    }
}
