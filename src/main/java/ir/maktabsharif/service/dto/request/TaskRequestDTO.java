package ir.maktabsharif.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskRequestDTO implements RequestDTO {
    @NotNull
    Long subCategoryId;
    @NotBlank
    @Size(min = 10, max = 300)
    String description;
    @NotBlank
    @Size(min = 20, max = 200)
    String locationAddress;
    LocalDateTime taskDateAndTime;

}
