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
public class UserEditProfileDTO implements RequestDTO {
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$",message = "invalid input pattern")
    String firstName;
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$")
    String lastName;
    @Email
    String email;
}
