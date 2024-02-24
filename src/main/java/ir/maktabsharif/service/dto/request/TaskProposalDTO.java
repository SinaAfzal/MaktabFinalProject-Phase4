package ir.maktabsharif.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskProposalDTO implements RequestDTO {
    @NotNull
    Long taskId;
    @NotNull
    Double proposedPrice;
    @NotNull
    Integer requiredHours;
    @NotNull
    LocalDateTime proposedStartTime;
}
