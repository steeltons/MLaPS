package org.jenjetsu.support;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResult {

    private CsvCategory category;

    private Float median;

    private Float standardDeviation;

    public CategoryResult add(CategoryResult another) {
        return builder()
            .category(this.category)
            .median(this.median + another.median)
            .standardDeviation(this.standardDeviation + another.standardDeviation)
            .build();
    }

    @Override
    public String toString() {
        return "Category=" + category + " median=" + median + " standard deviation=" + standardDeviation;
    }

}
