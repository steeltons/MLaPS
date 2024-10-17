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

}
