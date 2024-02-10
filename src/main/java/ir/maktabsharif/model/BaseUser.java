package ir.maktabsharif.model;


import ir.maktabsharif.model.enumeration.UserRole;
import jakarta.persistence.*;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@DiscriminatorColumn(name = "user-type")
public class BaseUser extends BaseEntity {
    //todo add field holding system messages
    //todo add entity for support tickets
    //todo add fields/entities to record history of each change in each entity
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$")
    String firstName;
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$")
    String lastName;
    @Enumerated(EnumType.STRING)
    UserRole role;
    @Email
    String email;
    String password;//since password is going to be hashed, validation of password is done in util.Validation class
    boolean isActive = true;// todo handle with email activation or other policies!
    LocalDateTime registrationDateTime;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserRole getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    @Override
    public String toString() {
        return "BaseUser{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", registrationDateTime=" + registrationDateTime +
                '}';
    }
}
