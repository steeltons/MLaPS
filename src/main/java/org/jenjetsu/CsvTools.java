package org.jenjetsu;

import java.util.*;

public class CsvTools {

    /**
     * Return median from sorted list of floats
     *
     * @param values sorted list of floats
     *
     * @return median
     */
    public static Float getMedian(List<Float> values) {
        var size = values.size();

        return (size % 2 != 0)
            ? values.get(size / 2)
            : (values.get(size / 2) + values.get(size / 2 + 1)) / 2;
    }

    /**
     * Count standard deviation from list of floats.<br>
     * Used formula: Sqrt((xi - ~X)^2 / N) <br>
     * Where: <br>
     *  xi - element in values <br>
     * ~X - average value of values <br>
     *  N - total values <br>
     * Formula site: https://ru.khanacademy.org/math/probability/data-distributions-a1/summarizing-spread-distributions/a/calculating-standard-deviation-step-by-step
     * @param values list of floats
     *
     * @return standard deviation
     */
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
