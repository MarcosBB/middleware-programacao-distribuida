package middleware.lifecycle;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeasingManager {

    private static final Set<RemoteInstance> leasableInstances = ConcurrentHashMap.newKeySet();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final long CHECK_INTERVAL = 3000; // Intervalo para verificar leases expirados

    public static void registerLeasableInstance(RemoteInstance instance) {
        if (instance != null && instance.isLeaseExpired()) {
            leasableInstances.add(instance);
        }
    }

    public static void startLeaseMonitor() {
        scheduler.scheduleAtFixedRate(() -> {
            leasableInstances.forEach(instance -> {
                if (instance.isLeaseExpired()) {
                    instance.destroy();
                }
            });
        }, 0, CHECK_INTERVAL, TimeUnit.MILLISECONDS); // Começa imediatamente, repete a cada X segundos
    }

    public static void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("LeasingManager: Scheduler de lease desligado.");
    }

}
