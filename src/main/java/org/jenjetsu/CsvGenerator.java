package org.jenjetsu;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class CsvGenerator implements Callable<String> {

    private static final int MAX_FILE_SIZE = 1_000_000;

    private static final Random random = new Random();

    private static final int categoryCount = CsvCategory.values().length;

    private static final List<String> HEADERS = List.of("value", "category");

    @Override
    public String call() {
        var filename = "test_" + Thread.currentThread().getName() + ".csv";
        var counter = 0;

        try (var writer = new FileWriter(filename)) {
            var headersLine = String.join(",", HEADERS);
            writer.write(headersLine + "\n");

            while (counter < MAX_FILE_SIZE) {
                var model = generateModel();
                writer.write(model.getValue() + "," + model.getCategory());
                if (counter < MAX_FILE_SIZE - 1) {
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
            .category(CsvCategory.values()[randomCategoryIndex].toString())
            .build();

        return model;
    }

}
