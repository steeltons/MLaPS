package org.jenjetsu.single;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jenjetsu.support.*;

public class SingleThreadMain {

    private static final int FILES_COUNT = 6;

    private static final int MAX_ELEMENTS = 10_000_000;

    public static void main(String[] args) {
        var categoryResultMap = Arrays.stream(CsvCategory.values())
            .map(category -> new CategoryResult(category, 0f, 0f))
            .collect(Collectors.toMap(CategoryResult::getCategory, Function.identity()));

        var start = LocalDateTime.now();
        var filenames = new ArrayList<String>();
        var generator = new CsvGenerator(MAX_ELEMENTS);

        for (var i = 0; i < FILES_COUNT; i++) {
            var filename = generator.call("file_" + i + ".csv");
            filenames.add(filename);
        }

        for (var filename : filenames) {
            var csvProcess = new CsvProcess(filename);
            csvProcess.call().forEach((category, result) -> {
                categoryResultMap.compute(category, (k, mapResult) -> result.add(mapResult));
            });
        }

        categoryResultMap.forEach((category, result) -> {
            System.out.println("Category=" + category + " median=" + result.getMedian() / FILES_COUNT
                               + " standard_deviation="
                               + result.getStandardDeviation() / FILES_COUNT);
        });

        var end = LocalDateTime.now();

        System.out.println("Total time: " + ChronoUnit.SECONDS.between(start, end));
    }

}
