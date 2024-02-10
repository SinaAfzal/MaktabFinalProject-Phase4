package ir.maktabsharif.model;


import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Proposal extends BaseEntity {
    @NotNull
    Long taskId;//unidirectional //todo needs to be indexed
    @NotNull
    Long tradesManId;//unidirectional //todo needs to be indexed
    @NotNull
    Double proposedPrice;
    @NotNull
    Integer requiredHours;
    LocalDateTime proposalRegistrationTime;
    @NotNull
    LocalDateTime proposedStartTime;
}
