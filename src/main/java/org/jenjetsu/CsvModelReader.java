package org.jenjetsu;

import java.io.*;
import java.util.*;

import lombok.*;

public class CsvModelReader {

    private final Scanner scanner;

    public CsvModelReader(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        try {
            scanner = new Scanner(new File(filename));
            scanner.nextLine(); // Skip headers
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasNext() {
        return scanner.hasNext();
    }

    public CsvModel getModel() {
        var words = scanner.nextLine().split(",");

        return CsvModel.builder()
            .value(Float.parseFloat(words[0]))
            .category(words[1])
            .build();
    }
}
