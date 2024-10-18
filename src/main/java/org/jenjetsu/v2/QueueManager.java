package org.jenjetsu.v2;

import java.util.*;
import java.util.concurrent.*;

import org.jenjetsu.single.*;
import org.jenjetsu.support.*;

import static org.jenjetsu.support.CsvCategory.*;

public class QueueManager {

    private static final int MAX_QUEUE_ELEMENTS = 1000;

    private static final QueueManager INSTANCE = new QueueManager();

    private final Map<CsvCategory, LinkedBlockingQueue<CsvModel>> map = Map.of(
        A, new LinkedBlockingQueue<>(MAX_QUEUE_ELEMENTS),
        B, new LinkedBlockingQueue<>(MAX_QUEUE_ELEMENTS),
        C, new LinkedBlockingQueue<>(MAX_QUEUE_ELEMENTS),
        D, new LinkedBlockingQueue<>(MAX_QUEUE_ELEMENTS)
    );

    private QueueManager() {}

    public boolean pushValue(CsvCategory category, CsvModel model) {
        var queue = map.get(category);
        boolean result = false;
        try {
            result = queue.offer(model, 2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public CsvModel getValue(CsvCategory category) {
        var queue = map.get(category);
        CsvModel result = null;
        try {
            result = queue.poll(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public boolean isQueueEmpty(CsvCategory category) {
        return map.get(category).isEmpty();
    }

    public static QueueManager getInstance() {
        return INSTANCE;
    }
}
