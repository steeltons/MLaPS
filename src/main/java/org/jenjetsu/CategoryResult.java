package org.jenjetsu;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResult {

    private String category;

    private Float median;

    private Float standardDeviation;

    public CategoryResult add(CategoryResult another) {
        return builder()
            .category(this.category)
            .median(this.median + another.median)
            .standardDeviation(this.standardDeviation + another.standardDeviation)
            .build();
    }

}
