package ir.maktabsharif.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue(value = "Customer")

public class Customer extends BaseUser {

    Double purchasedBalance;

    @Override
    public String toString() {
        return "Customer{" +
                "purchasedBalance=" + purchasedBalance +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", registrationDateTime=" + registrationDateTime +
                ", id=" + id +
                '}';
    }
}
