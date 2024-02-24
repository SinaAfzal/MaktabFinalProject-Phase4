package ir.maktabsharif.service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Builder
public class AdminRegistrationDTO implements RequestDTO {
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$")
    String firstName;
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$")
    String lastName;
    @Email(message = "Email pattern is not valid!")
    String email;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message = "Password is not Strong enough!")
    String notHashedPassword;
}
