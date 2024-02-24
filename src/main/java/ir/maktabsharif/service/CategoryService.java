package ir.maktabsharif.service;



import ir.maktabsharif.model.Category;
import ir.maktabsharif.service.dto.request.CategoryDTO;
import ir.maktabsharif.service.dto.response.FoundCategoryDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    boolean existsByCategoryName(String categoryName);
    FoundCategoryDTO findByCategoryName(String categoryName);
    FoundCategoryDTO findByCategoryNameIgnoreCase(String categoryName);
    Optional<Category> findByCategoryNameIgnoreCase_ForDeveloperUse(String categoryName);
    boolean isParent(Long categoryId);
    List<FoundCategoryDTO> findSubCategories(Long categoryId);
    List<FoundCategoryDTO> findAllParentCategories();
    void addCategory(CategoryDTO categoryDTO) throws InterruptedException;
    void editCategory(Long categoryId, CategoryDTO categoryDTO) throws InterruptedException;
    List<FoundCategoryDTO> findByTradesManId(Long tradesManId);
    void addTradesManToSubCategory(Long tradesManId,Long categoryId) throws InterruptedException;
    void removeTradesManFromSubCategory(Long tradesManId,Long categoryId) throws InterruptedException;
   void deleteById(Long categoryId);
    ResponseDTO mapToDTO(Category category);
}
