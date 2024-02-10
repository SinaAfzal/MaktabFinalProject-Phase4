package ir.maktabsharif.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Entity
@DiscriminatorValue(value = "Admin")
@AllArgsConstructor

public class Admin extends BaseUser{


}
