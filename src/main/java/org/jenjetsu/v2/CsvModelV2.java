package org.jenjetsu.v2;

import lombok.*;
import org.jenjetsu.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvModelV2 {

    private CsvCategory category;

    private Float value;

}
