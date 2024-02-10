package ir.maktabsharif.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Category extends BaseEntity {


    @NotBlank
    String categoryName;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    Category parentCategory;
    String description;
    Double basePrice;
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)//unidirectional
    @JoinTable(name = "category_tradesmen")
    Set<TradesMan> tradesMen = new HashSet<>(); //todo entitygraph


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String name) {
        this.categoryName = name;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Set<TradesMan> getTradesMen() {
        if (tradesMen == null) {
            tradesMen = new HashSet<>();
        }
        return tradesMen;
    }

    public void setTradesMen(Set<TradesMan> tradesMen) {
        this.tradesMen = tradesMen;
    }
}
