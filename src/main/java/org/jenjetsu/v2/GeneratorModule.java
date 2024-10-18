package org.jenjetsu.v2;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import org.jenjetsu.tool.*;

public class GeneratorModule {

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
            .map(ignore -> new CsvGenerator())
            .map(executorService::submit)
            .collect(Collectors.toSet());

        executorService.shutdown();
        return futures;
    }
}
