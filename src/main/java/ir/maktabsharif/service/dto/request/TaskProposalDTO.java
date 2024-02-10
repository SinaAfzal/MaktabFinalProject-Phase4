package ir.maktabsharif.service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskProposalDTO implements RequestDTO {
    Long taskId;
    Double proposedPrice;
    Integer requiredHours;
    LocalDateTime proposedStartTime;
}
