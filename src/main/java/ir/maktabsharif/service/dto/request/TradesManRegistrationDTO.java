package ir.maktabsharif.service.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradesManRegistrationDTO implements RequestDTO {
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$")
    String firstName;
    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{3,15}(?<!\\s)$")
    String lastName;
    @Email
    String email;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    String password;
    String imagePath;
}
