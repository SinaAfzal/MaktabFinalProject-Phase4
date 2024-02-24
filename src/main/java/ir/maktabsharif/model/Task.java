package ir.maktabsharif.model;


import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.util.exception.AccessDeniedException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Table(name = "tasks")
public class Task extends BaseEntity {

    Long subCategoryId;//todo needs to be indexed
    @ManyToOne
    TradesMan tradesManWhoGotTheJob;//todo instead of tradesman you should put the winner proposal
    @OneToOne
    Proposal selectedProposal;
    @ManyToOne
    Customer customer;
    @NotBlank
    @Size(min = 10, max = 300)
    String description;
    @Max(value = 5)
    @Min(value = 1)
    Float score;
    @Size(max = 300)
    String comment;
    @NotBlank
    @Size(min = 20, max = 200)
    String locationAddress;
    LocalDateTime requestDateTime;
    LocalDateTime taskDateTimeByCustomer;
    LocalDateTime dateTimeOfBeingDone;
    @Enumerated(EnumType.STRING)
    TaskStatus status;

    @Override
    public String toString() {
        return "Task{" +
                "subCategoryId=" + subCategoryId +
                ", tradesManWhoGotTheJob=" + tradesManWhoGotTheJob +
                ", customer=" + customer +
                ", description='" + description + '\'' +
                ", score=" + score +
                ", comment='" + comment + '\'' +
                ", locationAddress='" + locationAddress + '\'' +
                ", requestDateTime=" + requestDateTime +
                ", taskDateTimeByCustomer=" + taskDateTimeByCustomer +
                ", dateTimeOfBeingDone=" + dateTimeOfBeingDone +
                ", taskStatus=" + status +
                ", id=" + id +
                '}';
    }
}
