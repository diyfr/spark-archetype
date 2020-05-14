#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.service;

import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.inject.Singleton;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

@Singleton
public class HealthController {
    private static final String PATH = "health/";
    private static final String METRICS = PATH + "metrics";
    private static final String STATUS = PATH + "status";

    public void init() {
        Spark.get(METRICS, getMetrics);
        Spark.get(METRICS, getStatus);
    }

    private final Route getStatus = (Request request, Response response) -> {
        response.status(HttpStatus.OK_200);
        return "UP";
    };

    private final Route getMetrics = (Request request, Response response) -> {
        long heapSize = Runtime.getRuntime().totalMemory();
        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        long totalGarbageCollections = 0;
        long garbageCollectionTime = 0;
        for (GarbageCollectorMXBean gc :
                ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gc.getCollectionCount();
            gc.getObjectName();
            if (count >= 0) {
                totalGarbageCollections += count;
            }
            long time = gc.getCollectionTime();
            if (time >= 0) {
                garbageCollectionTime += time;
            }
        }
        response.header("Content-Type", "application/json; charset=utf-8");
        response.status(HttpStatus.OK_200);
        return "{" + "\"heapSize\":\"" +
                formatSize(heapSize) +
                "\", " +
                "\"heapMapSize\":\"" +
                formatSize(heapMaxSize) +
                "\", " +
                "\"heapFreeSize\":\"" +
                formatSize(heapFreeSize) +
                "\", " +
                "\"totalGarbageCollections\":" +
                totalGarbageCollections +
                ", " +
                "\"garbageCollectionTime\":" +
                garbageCollectionTime +
                "}";
    };


    private static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
