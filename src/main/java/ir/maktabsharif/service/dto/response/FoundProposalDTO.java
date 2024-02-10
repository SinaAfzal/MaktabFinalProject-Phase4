package ir.maktabsharif.service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@ToString
public class FoundProposalDTO implements ResponseDTO {
    Long id;
    Long taskId;//unidirectional
    Long tradesManId;//unidirectional
    Double proposedPrice;
    Integer requiredHours;
    LocalDateTime proposalRegistrationTime;
    LocalDateTime proposedStartTime;
}
