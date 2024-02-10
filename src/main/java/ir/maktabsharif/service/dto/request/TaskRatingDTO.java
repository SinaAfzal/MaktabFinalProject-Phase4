package ir.maktabsharif.service.dto.request;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskRatingDTO implements RequestDTO {
    Long taskId;
    @Size(max = 300)
    String comment;
    @Max(value = 5)
    @Min(value = 1)
    Float rating;
}
