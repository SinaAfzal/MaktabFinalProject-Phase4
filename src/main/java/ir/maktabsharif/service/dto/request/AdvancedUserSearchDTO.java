package ir.maktabsharif.service.dto.request;

import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Builder
public class AdvancedUserSearchDTO implements RequestDTO {
    //generic
    String firstName;
    String lastName;
    UserRole role;
    String email;
    Boolean isActive;
    /**
     * @Author(Sina_Afzalsoltani) ----->Note that in the BaseUser Entity, the registrationDateTime is in 'LocalDateTime'
     * format. But in this search DTO the registration time is in 'LocalDate' format.
     * <p>
     * you can compare a 'LocalDate' with a 'LocalDateTime' in the CriteriaBuilder.
     * When comparing a 'LocalDate' with a 'LocalDateTime',the 'LocalDate' will be
     * implicitly converted to a 'LocalDateTime' at the start of the day (midnight)
     * before the comparison is made.
     * <p>
     * you can use the @DateTimeFormat annotation in your Spring REST controller.
     * Here's an example of how to do this:
     * <code>
     *
     * @PostMapping("/date") public void date(@RequestParam("localDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate) {
     * // Your code here
     * }
     *
     * <code/>
     * this way the format in json to be passed to postman would be like: For example, "2024-02-05"
     */
    LocalDate registrationDateFrom;
    LocalDate registrationDateTo;

    //tradesMan specific:
    TradesManStatus tradesManStatus;
    Float tradesManRatingMin;
    Float tradesManRatingMax;
    Double tradesManEarnedCreditMin;
    Double tradesManEarnedCreditMax;
    Long tradesManSubCategoryId;

    //customer specific:
    Double customerPurchasedBalanceMin;
    Double customerPurchasedBalanceMax;

    //following getter is written manually because in case of boolean primitive type variable LOMBOK getter would be isActive() as the case in the BaseUser entity. but here the Boolean reference type variable is used and LOMBOK getter for it would be getIsActive(). in order to maintain the same method names across the application this getter is manually written.
    public Boolean isActive(){
        return this.isActive;
    }
}
