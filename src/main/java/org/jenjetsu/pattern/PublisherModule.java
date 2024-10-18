package org.jenjetsu.pattern;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.slf4j.*;

import lombok.*;
import org.jenjetsu.single.*;
import org.jenjetsu.support.*;

public class PublisherModule implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PublisherModule.class);

    private final QueueManager queue = QueueManager.getInstance();

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private final AtomicBoolean globalStopFlag;

    private final Set<Future<String>> generatorFutures;

    public PublisherModule(Collection<Future<String>> generatorFutures, AtomicBoolean globalStopFlag) {
        this.generatorFutures = new HashSet<>(generatorFutures);
        this.globalStopFlag = globalStopFlag;
    }

    @SneakyThrows
    public void run() {
        var publishers = new ArrayList<Future<?>>();

        do {
            var usedFutures = new HashSet<Future<String>>();
            for (var future : generatorFutures) {
                if (future.isDone()) {
                    var filename = future.get();
                    var reader = new PublisherWorker(filename);
                    publishers.add(executorService.submit(reader));
                    usedFutures.add(future);
                }
            }
            generatorFutures.removeAll(usedFutures);
        } while (!generatorFutures.isEmpty());

        while (!isWorkDone(publishers)) {
            Thread.sleep(500L);
        }
        executorService.shutdown();
        globalStopFlag.set(true);
    }

    private boolean isWorkDone(List<Future<?>> workers) {
        return workers.stream().allMatch(Future::isDone);
    }

    private class PublisherWorker implements Runnable {

        private final Scanner scanner;

        @SneakyThrows
        public PublisherWorker(String filename) {
            scanner = new Scanner(Paths.get(filename));
        }

        @Override
        public void run() {
            logger.info("Reader publisher job start");
            scanner.nextLine(); // Skip headers
            var pushResult = false;
            while (scanner.hasNext()) {
                var model = parseModel(scanner.nextLine());
                do {
                    pushResult = queue.pushValue(model.getCategory(), model);
                } while (!pushResult);
                pushResult = false;
            }
            logger.info("Reader publisher job done");
        }

        private CsvModel parseModel(String line) {
            var words = line.split(",");

            return CsvModel.builder()
                .value(Float.parseFloat(words[0]))
                .category(CsvCategory.valueOf(words[1]))
                .build();
        }

    }
}
