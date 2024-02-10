package ir.maktabsharif.service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class SelectTradesManProposalDTO implements RequestDTO {
    Long proposalId;
    Long taskId;
}
