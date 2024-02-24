package ir.maktabsharif.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Entity
@SuperBuilder
public class EmailConfirmationToken extends BaseEntity {
    String tokenSerial;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    LocalDateTime confirmedAt;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    BaseUser baseUser;
}
