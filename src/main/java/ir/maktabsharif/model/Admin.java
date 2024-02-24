package ir.maktabsharif.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;


@Entity
@DiscriminatorValue(value = "Admin")
@AllArgsConstructor
@SuperBuilder
public class Admin extends BaseUser{


}
