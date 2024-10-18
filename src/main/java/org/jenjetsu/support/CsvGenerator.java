package org.jenjetsu.support;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.jenjetsu.single.*;

public class CsvGenerator implements Callable<String> {

    private static final Random random = new Random();

    private static final int categoryCount = CsvCategory.values().length;

    /** Csv headers **/
    private static final List<String> HEADERS = List.of("value", "category");

    public final int maxElements;

    public CsvGenerator(int maxElements) {
        if (maxElements <= 0) {
            throw new IllegalArgumentException("Count of elements cannot be less or eq zero");
        }
        this.maxElements = maxElements;
    }

    @Override
    public String call() {
        var filename = "test_" + Thread.currentThread().getName() + ".csv";
        return call(filename);
    }

    public String call(String filename) {
        var counter = 0;

        try (var writer = new FileWriter(filename)) {
            var headersLine = String.join(",", HEADERS);
            writer.write(headersLine + "\n");

            while (counter < maxElements) {
                var model = generateModel();
                writer.write(model.getValue() + "," + model.getCategory().name());
                if (counter < maxElements - 1) {
                    writer.write("\n");
                }

                counter++;
            }

            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CsvModel generateModel() {
        var randomValue = random.nextFloat();
        var randomCategoryIndex = random.nextInt(0, categoryCount);
        var model = CsvModel.builder()
            .value(randomValue)
            .category(CsvCategory.values()[randomCategoryIndex])
            .build();

        return model;
    }

}
