package ir.maktabsharif.service.dto.request;



import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@ToString
public class PaymentDTO implements RequestDTO{
    @Pattern(regexp = "^\\d{2,5}$")
    String cvv;
    @Pattern(regexp = "^[1-9][0-9]{15}$")
    String cardNo;
    @Pattern( regexp = "^[0-9]{2}$")
    String year;
    @Pattern(regexp = "^[0-1][1-9]$")
    String month;
    @Pattern(regexp = "^\\d{6,10}$")
    String password;
}
