package ir.maktabsharif.service.dto.response;

import ir.maktabsharif.model.enumeration.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@ToString
public class FoundCustomerDTO implements ResponseDTO {
    Long id;
    String firstName;
    String lastName;
    UserRole role;
    String email;
    boolean isActive;
    LocalDateTime registrationDateTime;
    Double purchasedBalance;
    Long numberOfRequestedTasks;
    Long numberOfDoneTasks;
    boolean isEmailVerified;
}
