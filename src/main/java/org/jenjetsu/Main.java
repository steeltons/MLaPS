package org.jenjetsu;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import org.slf4j.*;

import org.jenjetsu.support.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int MAX_ELEMENTS = 10_000_00;

    /**
     * For check single thread time usage - set thread to 1<br>
     * For multithreading - set another
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var finalCategoryResult = Arrays.stream(CsvCategory.values())
            .map(name -> new CategoryResult(name.name(), 0f, 0f))
            .collect(Collectors.toMap(CategoryResult::getCategory, Function.identity()));

        var start = LocalDateTime.now();

        var generatorFutures = getFilenameFutures();
        var processFutures = getProcessCallFutures(generatorFutures);

        // Obtain result from futures
        for (var future : processFutures) {
            var result = future.get();

            result.forEach((category, categoryResult) -> {
                finalCategoryResult.computeIfPresent(category, (k, v) -> v.add(categoryResult));
            });
        }

        finalCategoryResult.values().forEach(result -> {
            var category = result.getCategory();
            var median = result.getMedian() / processFutures.size();
            var standardDeviation = result.getStandardDeviation() / processFutures.size();

            logger.info("Category={}, median={}, standard deviation={}", category, median, standardDeviation);
        });

        executorService.shutdown();

        var end = LocalDateTime.now();

        logger.info("Used time : {} seconds", ChronoUnit.SECONDS.between(start, end));

    }

    /**
     * Create 6 file generators and send then to executor
     * @return list of futures that returns filenames
     * @throws InterruptedException
     */
    private static List<Future<String>> getFilenameFutures() throws InterruptedException {
        var generatorCalls = new ArrayList<Callable<String>>();

        for (var i = 0; i < 6; i++) {
            var generatorCall = new CsvGenerator(MAX_ELEMENTS);
            generatorCalls.add(generatorCall);
        }
        return executorService.invokeAll(generatorCalls);
    }

    /**
     *
     * @param generatorFutures list of futures that returns filenames
     * @return list of futures that returns result per category
     * @throws ExecutionException
     * @throws InterruptedException
     */
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