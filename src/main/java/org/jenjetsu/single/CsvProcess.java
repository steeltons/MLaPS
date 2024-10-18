package org.jenjetsu.single;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import org.jenjetsu.support.*;

public class CsvProcess implements Callable<Map<CsvCategory, CategoryResult>> {

    private final CsvModelReader modelReader;

    public CsvProcess(String filename) {
        modelReader = new CsvModelReader(filename);
    }

    @Override
    public Map<CsvCategory, CategoryResult> call() {
        var categoryModelMap = readAndGroupModelsByCategory();

        var categoryResults = categoryModelMap.entrySet().stream()
            .map(pair -> {
                var values = pair.getValue().stream()
                    .map(CsvModel::getValue)
                    .sorted(Float::compareTo)
                    .toList();
                var median = CsvTools.getMedian(values, false);
                var standardDeviation = CsvTools.getStandardDeviation(values, false);

                return CategoryResult.builder()
                    .category(pair.getKey())
                    .median(median)
                    .standardDeviation(standardDeviation)
                    .build();
            })
            .collect(Collectors.toMap(CategoryResult::getCategory, Function.identity()));

        return categoryResults;
    }

    /**
     * Read all records from file and group results by category
     * @return map of category and its params
     */
    private Map<CsvCategory, List<CsvModel>> readAndGroupModelsByCategory() {
        var categoryModelMap = new HashMap<CsvCategory, List<CsvModel>>();

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
