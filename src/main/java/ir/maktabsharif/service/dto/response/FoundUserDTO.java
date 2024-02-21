package ir.maktabsharif.service.dto.response;

import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class FoundUserDTO implements ResponseDTO{
    Long id;
    String firstName;
    String lastName;
    UserRole role;
    String email;
    boolean isActive;
    LocalDateTime registrationDateTime;
    boolean isEmailVerified;

    //for customers:
    Double purchasedBalance;
    Long numberOfRequestedTasks;

    //for customers and tradesmen:
    Long numberOfDoneTasks;

    //for tradesmen:
    Long numberOfProposalsSent;
    TradesManStatus status;
    byte[] avatar;
    Float rating;
    Double earnedCredit;
}
