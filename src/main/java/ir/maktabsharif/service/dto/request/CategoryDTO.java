package ir.maktabsharif.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class CategoryDTO implements RequestDTO {
    @NotBlank
    String name;
    Long parentCategoryId;
    String description;
    Double basePrice;
}
