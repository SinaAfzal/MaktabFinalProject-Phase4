package ir.maktabsharif.service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class FoundCategoryDTO implements ResponseDTO {
    Long id;
    String name;
    Long parentCategoryId;
    String description;
    Double basePrice;
    Set<Long> tradesManIds = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
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

    public Set<Long> getTradesManIds() { //todo this is to avoid fetch lazily exception
        if (tradesManIds == null)
            tradesManIds = new HashSet<>();
        return tradesManIds;
    }

    public void setTradesManIds(Set<Long> tradesManIds) {
        this.tradesManIds = tradesManIds;
    }
}
