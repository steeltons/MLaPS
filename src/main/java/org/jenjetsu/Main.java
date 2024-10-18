package org.jenjetsu;

import java.util.concurrent.*;

import org.jenjetsu.single.*;
import org.jenjetsu.v2.*;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        SingleThreadMain.main(args);
        MultiThreadMain.main(args);
    }

}