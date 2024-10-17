package org.jenjetsu;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import org.slf4j.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * To check single thread time usage - set thread to 1
     * To multithreading - set another
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        var start = LocalDateTime.now();

        var generatorFutures = getFilenameFutures();

        var processFutures = getProcessCallFutures(generatorFutures);

        var categoryMap = Arrays.stream(CsvCategory.values())
            .map(name -> new CategoryResult(name.name(), 0f, 0f))
            .collect(Collectors.toMap(CategoryResult::getCategory, Function.identity()));

        // Obtain result from futures
        for (var future : processFutures) {
            var result = future.get();

            result.forEach((category, categoryResult) -> {
                var categoryBucket = categoryMap.get(category);
                categoryBucket.setMedian(categoryBucket.getMedian() + categoryResult.getMedian());
                categoryBucket.setStandardDeviation(categoryBucket.getStandardDeviation() + categoryResult.getStandardDeviation());
            });
        }

        categoryMap.values().forEach(result -> {
            var category = result.getCategory();
            var median = result.getMedian() / processFutures.size();
            var standardDeviation = result.getStandardDeviation() / processFutures.size();

            System.out.println(category + " " + median + " " + standardDeviation);
        });

        executorService.shutdown();

        var end = LocalDateTime.now();

        logger.info("Used time : {} seconds", ChronoUnit.SECONDS.between(start, end));

    }

    private static List<Future<String>> getFilenameFutures() throws InterruptedException {
        var generatorCalls = new ArrayList<Callable<String>>();

        for (var i = 0; i < 6; i++) {
            var generatorCall = new CsvGenerator();
            generatorCalls.add(generatorCall);
        }
        return executorService.invokeAll(generatorCalls);
    }

    private static List<Future<Map<String, CategoryResult>>> getProcessCallFutures(
        List<Future<String>> generatorFutures
    ) throws ExecutionException, InterruptedException {
        var processCalls = new ArrayList<Callable<Map<String, CategoryResult>>>();

        for (var future : generatorFutures) {
            var filename = future.get();
            var processCall = new CsvProcess(filename);

            processCalls.add(processCall);
        }

        return executorService.invokeAll(processCalls);
    }

}