package org.jenjetsu;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class CsvProcess implements Callable<Map<String, CategoryResult>> {

    private final CsvModelReader modelReader;

    public CsvProcess(String filename) {
        modelReader = new CsvModelReader(filename);
    }

    @Override
    public Map<String, CategoryResult> call() {
        var categoryModelMap = readModelsByCategory();

        var categoryResults = categoryModelMap.entrySet().stream()
            .map(pair -> {
                var values = pair.getValue().stream()
                    .map(CsvModel::getValue)
                    .sorted(Float::compareTo)
                    .toList();
                var median = CsvTools.getMedian(values);
                var standardDeviation = CsvTools.countStandardDeviation(values);

                return CategoryResult.builder()
                    .category(pair.getKey())
                    .median(median)
                    .standardDeviation(standardDeviation)
                    .build();
            })
            .collect(Collectors.toMap(CategoryResult::getCategory, Function.identity()));

        return categoryResults;
    }

    private Map<String, List<CsvModel>> readModelsByCategory() {
        var categoryModelMap = new HashMap<String, List<CsvModel>>();

        while (modelReader.hasNext()) {
            var model = modelReader.getModel();
            if (!categoryModelMap.containsKey(model.getCategory())) {
                categoryModelMap.put(model.getCategory(), new ArrayList<>());
            }
            categoryModelMap.get(model.getCategory()).add(model);
        }

        return categoryModelMap;
    }

}
