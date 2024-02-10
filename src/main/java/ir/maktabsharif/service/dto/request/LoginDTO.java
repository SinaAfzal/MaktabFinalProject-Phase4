package ir.maktabsharif.service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginDTO implements RequestDTO {
    String email;
    String notHashedPassword;
}
