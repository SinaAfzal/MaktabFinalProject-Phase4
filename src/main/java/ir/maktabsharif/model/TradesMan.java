package ir.maktabsharif.model;

import ir.maktabsharif.model.enumeration.TradesManStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue(value = "TradesMan")
public class TradesMan extends BaseUser {

    @Enumerated(EnumType.STRING)
    TradesManStatus status;
    byte[] avatar;
    Float rating=0F;
    Double earnedCredit;
//todo you had to put the ManyToMany relation with categories here! not in the categories :(

    @Override
    public String toString() {
        return "TradesMan{" +
                "status=" + status +
                ", rating=" + rating +
                ", earnedCredit=" + earnedCredit +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", registrationDateTime=" + registrationDateTime +
                ", Id=" + id +
                '}';
    }
}
