package org.jenjetsu.single;

import lombok.*;
import org.jenjetsu.support.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvModel {

    private Float value;

    private CsvCategory category;

}
