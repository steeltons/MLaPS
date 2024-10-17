package org.jenjetsu;

import java.util.*;
import java.util.function.*;

public class CsvTools {

    public static Float getMedian(List<Float> values) {
        var size = values.size();

        return (size % 2 != 0)
            ? values.get(size / 2)
            : (values.get(size / 2) + values.get(size / 2 + 1)) / 2;
    }

    public static Float countStandardDeviation(List<Float> values) {
        var size = values.size();
        var valuesSum = values.stream().reduce(0f, Float::sum);
        var averageValue = valuesSum / size;

        var newSum = values.stream()
            .map(val -> (float) Math.pow(val - averageValue, 2))
            .reduce(0f, Float::sum);

        var result = (float) Math.sqrt(newSum);

        return result;
    }
}
