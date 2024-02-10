package ir.maktabsharif.service.dto.response;

import ir.maktabsharif.model.enumeration.TaskStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@ToString
public class FoundTaskDTO implements ResponseDTO {
    Long id;
    Long subCategoryId;
    Long tradesManWhoGotTheJobId;
    Long customerId;
    String description;
    Float score;
    String comment;
    String locationAddress;
    LocalDateTime requestDateTime;
    LocalDateTime taskDateTimeByCustomer;
    LocalDateTime dateTimeOfBeingDone;
    TaskStatus taskStatus;
}
