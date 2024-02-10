package ir.maktabsharif.service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskRequestDTO implements RequestDTO {

    Long subCategoryId;
    String description;
    String locationAddress;
    LocalDateTime taskDateAndTime;

}
