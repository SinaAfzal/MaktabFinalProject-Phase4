package ir.maktabsharif.model;

import ir.maktabsharif.model.enumeration.TradesManStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue(value = "TradesMan")
@SuperBuilder
public class TradesMan extends BaseUser {

    @Enumerated(EnumType.STRING)
    @Builder.Default
    TradesManStatus status = TradesManStatus.NEW;
    byte[] avatar;
    @Builder.Default
    Float rating = 0F;
    @Builder.Default
    Double earnedCredit = 0D;
    @Builder.Default
    Long numberOfDoneTasks = 0L;
    @Builder.Default
    Long numberOfProposalsSent = 0L;
//todo you had to put the ManyToMany relation with categories here! not in the categories :(


    @Override
    public String toString() {
        return "TradesMan{" +
                "status=" + status +
                ", avatar=" + Arrays.toString(avatar) +
                ", rating=" + rating +
                ", earnedCredit=" + earnedCredit +
                ", numberOfDoneTasks=" + numberOfDoneTasks +
                ", numberOfProposalsSent=" + numberOfProposalsSent +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", isEmailVerified=" + isEmailVerified +
                ", registrationDateTime=" + registrationDateTime +
                ", id=" + id +
                '}';
    }
}
