package org.jenjetsu.v2;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.slf4j.*;

import lombok.*;
import org.jenjetsu.single.*;
import org.jenjetsu.support.*;

@RequiredArgsConstructor
public class SubscriberModule implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberModule.class);

    private static final QueueManager queue = QueueManager.getInstance();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(
        CsvCategory.values().length);

    private final AtomicBoolean outerStopFlag;

    @Override
    @SneakyThrows
    public void run() {
        var futures = Arrays.stream(CsvCategory.values())
            .map(SubscriberWorker::new)
            .map(executorService::submit)
            .toList();

        for (var future : futures) {
            var result = future.get();

            logger.info("Category={} median={} standard_deviation={}", result.getCategory(),
                result.getMedian(), result.getStandardDeviation());
        }

        executorService.shutdown();
    }

    @RequiredArgsConstructor
    private class SubscriberWorker implements Callable<CategoryResult> {

        private final List<Float> values = new ArrayList<>();

        private final CsvCategory category;

        @Override
        public CategoryResult call() throws Exception {
            logger.info("{} subscriber job start", category.name());
            while (!(outerStopFlag.get() && queue.isQueueEmpty(category))) {
                var model = queue.getValue(category);
                if (model == null) {
                    continue;
                }
                processModel(model);
            }

            logger.info("{} subscriber job done", category.name());
            return getResult();
        }

        private void processModel(CsvModel model) {
            values.add(model.getValue());
        }

        private CategoryResult getResult() {
            var median = CsvTools.getMedian(values, true);
            var standardDeviation = CsvTools.getStandardDeviation(values, true);

            return CategoryResult.builder()
                .category(this.category)
                .median(median)
                .standardDeviation(standardDeviation)
                .build();
        }

    }

}
