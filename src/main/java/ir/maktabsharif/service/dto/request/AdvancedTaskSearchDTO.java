package ir.maktabsharif.service.dto.request;

import ir.maktabsharif.model.enumeration.TaskStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@ToString
@Builder
public class AdvancedTaskSearchDTO {
    Long parentCategoryId;
    Long subCategoryId;
    Long customerId;
    Long tradesManId;
    LocalDate requestDateFrom;
    LocalDate requestDateTo;
    LocalDate taskStartDateFrom;
    LocalDate taskStartDateTo;
    TaskStatus status;
    Double winnerPriceMin;
    Double winnerPriceMax;
}
