package org.jenjetsu.pattern;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import org.jenjetsu.support.*;

public class GeneratorModule {

    private static final int MAX_ELEMENTS = 10_000_000;

    private final ExecutorService executorService;

    private final int generatorCount;

    public GeneratorModule(int generatorCount) {
        if (generatorCount <= 0) {
            throw new IllegalArgumentException("Count of generators cannot be less or eq zero");
        }
        this.generatorCount = generatorCount;
        this.executorService = Executors.newFixedThreadPool(generatorCount);
    }

    public Set<Future<String>> start() {
        var futures = IntStream.range(0, generatorCount).boxed()
            .map(ignore -> new CsvGenerator(MAX_ELEMENTS))
            .map(executorService::submit)
            .collect(Collectors.toSet());

        executorService.shutdown();
        return futures;
    }
}
