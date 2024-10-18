package org.jenjetsu.support;

import java.util.*;

public class CsvTools {

    /**
     * Return median from sorted list of floats
     *
     * @param values list of values
     *
     * @return median
     */
    public static Float getMedian(List<Float> values, boolean useParallel) {
        if (values.isEmpty()) {
            return null;
        }

        var sortedStream = (useParallel)
            ? values.stream().parallel().sorted(Float::compareTo)
            : values.stream().sorted(Float::compareTo);
        var size = values.size();

        return (size % 2 != 0)
            ? sortedStream.skip((size - 1) / 2).findFirst().orElse(0f)
            : (sortedStream.skip((size - 1) / 2).limit(2).reduce(0f, Float::sum)) / 2;
    }

    public static Float getMean(Collection<Float> values, boolean useParallel) {
        var sum = (useParallel)
            ? values.stream().parallel().reduce(0f, Float::sum)
            : values.stream().reduce(0f, Float::sum);

        return sum / values.size();
    }

    public static Float getVariance(Collection<Float> values, boolean useParallel) {
        var mean = getMean(values, useParallel);
        var temp = (useParallel)
            ? values.stream().parallel().map(val -> (float) Math.pow(val - mean, 2)).reduce(0f, Float::sum)
            : values.stream().map(val -> (float) Math.pow(val - mean, 2)).reduce(0f, Float::sum);

        return temp / (values.size() - 1);
    }

    public static Float getStandardDeviation(Collection<Float> values, boolean useParallel) {
        return (float) Math.sqrt(getVariance(values, useParallel));
    }

}
