package org.jenjetsu.pattern;

import java.time.*;
import java.time.temporal.*;
import java.util.concurrent.atomic.*;

import lombok.*;

public class MultiThreadMain {

    private static final AtomicBoolean stopFlag = new AtomicBoolean(false);

    @SneakyThrows
    public static void main(String[] args) {
        var startTime = LocalDateTime.now();
        var generatorModule = new GeneratorModule(6);
        var generatorFutures = generatorModule.start();

        var publisherModule = new PublisherModule(generatorFutures, stopFlag);
        var subscriberModule = new SubscriberModule(stopFlag);

        var publisherThread = new Thread(publisherModule);
        var subscriberThread = new Thread(subscriberModule);

        publisherThread.start();
        subscriberThread.start();

        publisherThread.join();
        subscriberThread.join();
        var endTime = LocalDateTime.now();

        System.out.println("Total time: " + ChronoUnit.SECONDS.between(startTime, endTime));
    }

}
