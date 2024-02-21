package ir.maktabsharif.model;


import ir.maktabsharif.model.enumeration.UserRole;
import jakarta.persistence.*;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@DiscriminatorColumn(name = "user-type")
public class BaseUser extends BaseEntity implements UserDetails {
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
    boolean isActive = false;
    boolean isEmailVerified=false;
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

    public boolean isEmailVerified() {
        return isEmailVerified;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEmailVerified;
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
    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
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
