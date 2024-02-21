package ir.maktabsharif.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@ToString
@Builder
public class Email {
    String subject;
    String from;
    String to;
    String body;
}
