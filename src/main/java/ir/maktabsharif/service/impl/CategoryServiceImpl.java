package ir.maktabsharif.service.impl;


import ir.maktabsharif.model.Category;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.repository.CategoryRepository;
import ir.maktabsharif.service.CategoryService;
import ir.maktabsharif.service.dto.request.CategoryDTO;
import ir.maktabsharif.service.dto.response.FoundCategoryDTO;
import ir.maktabsharif.util.ApplicationContext;
import ir.maktabsharif.util.SemaphoreUtil;
import ir.maktabsharif.util.exception.ExistingEntityCannotBeFetchedException;
import ir.maktabsharif.util.exception.InvalidInputException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CategoryServiceImpl implements
        CategoryService {
    private final CategoryRepository repository;


    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        return repository.existsByCategoryName(categoryName);
    }

    @Override
    public FoundCategoryDTO findByCategoryName(String categoryName) {
        if (!existsByCategoryName(categoryName))
            throw new NoSuchElementException("Category not found!");
        return mapToDTO(repository.findByCategoryName(categoryName));
    }

    @Override
    public FoundCategoryDTO findByCategoryNameIgnoreCase(String categoryName) {
        Optional<Category> categoryOptional = repository.findByCategoryNameIgnoreCase(categoryName);
        if (categoryOptional.isEmpty())
            return null;
        return mapToDTO(categoryOptional.get());
    }

    @Override
    public Optional<Category> findByCategoryNameIgnoreCase_ForDeveloperUse(String categoryName) {
        return repository.findByCategoryNameIgnoreCase(categoryName);
    }

    @Override
    public boolean isParent(Long categoryId) {
        boolean doesExist = repository.existsById(categoryId);
        if (!doesExist)
            throw new InvalidInputException("Category does not exist!");
        return repository.isParent(categoryId);
    }

    @Override
    public List<FoundCategoryDTO> findSubCategories(Long categoryId) {
        boolean isParent = repository.isParent(categoryId);
        if (!isParent)
            throw new InvalidInputException("Category is not a parent category!");
        List<Category> subCategories = repository.findSubCategories(categoryId);
        List<FoundCategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category c : subCategories)
            categoryDTOS.add(mapToDTO(c));
        return categoryDTOS;
    }

    @Override
    public List<FoundCategoryDTO> findAllParentCategories() {
        List<Category> parentCategories = repository.findAllParentCategories();
        List<FoundCategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category c : parentCategories)
            categoryDTOS.add(mapToDTO(c));
        return categoryDTOS;
    }

    @Transactional
    @Override
    public void addCategory(CategoryDTO categoryDTO) throws InterruptedException {
        SemaphoreUtil.acquireNewCategorySemaphore();
        try {
            String categoryName = categoryDTO.getName();
            if (existsByCategoryName(categoryName))
                throw new InvalidInputException("Duplicate category names are not allowed!");
            String categoryDescription = categoryDTO.getDescription();
            Double categoryBasePrice = categoryDTO.getBasePrice();
            Long parentCategoryId = categoryDTO.getParentCategoryId();
            boolean parentIdExists = false;
            if (parentCategoryId != null)
                parentIdExists = repository.existsById(parentCategoryId);
            Category categoryToBeSaved = Category.builder().
                    categoryName(categoryName).
                    description(categoryDescription).
                    basePrice(categoryBasePrice).
                    build();
            if (parentCategoryId == null) {
                if (categoryBasePrice != null || categoryDescription != null)
                    throw new InputMismatchException("Only sub-categories can take base price or description!");
                repository.save(categoryToBeSaved);
            } else if (!parentIdExists)
                throw new InvalidInputException("Parent Id is not valid");
            else {
                Optional<Category> parentCategoryOptional = repository.findById(parentCategoryId);
                if (parentCategoryOptional.isEmpty())
                    throw new ExistingEntityCannotBeFetchedException("Parent category exists but cannot be fetched!");
                else {
                    if (categoryBasePrice == null || categoryDescription.isBlank())
                        throw new InputMismatchException("sub-categories must have a basePrice and description!");
                    categoryToBeSaved.setParentCategory(parentCategoryOptional.get());
                    Set<ConstraintViolation<Category>> violations = ApplicationContext.getValidator().validate(categoryToBeSaved);
                    if (!violations.isEmpty())
                        throw new ConstraintViolationException(violations);

                    repository.save(categoryToBeSaved);

                }
            }
        } finally {
            SemaphoreUtil.releaseNewCategorySemaphore();
        }
    }

    @Transactional
    @Override
    public void editCategory(Long categoryId, CategoryDTO categoryDTO) throws InterruptedException {
        SemaphoreUtil.acquireNewCategorySemaphore();
        try {
            //check to see if the categoryId exists and fetch it.
            if (!repository.existsById(categoryId))
                throw new InvalidInputException("Invalid category ID!");
            Category categoryToBeUpdated = repository.findById(categoryId).
                    orElseThrow(
                            () -> new ExistingEntityCannotBeFetchedException(
                                    "The category you want to edit exists but cannot be fetched!")
                    );
            // create a new category based on dto
            String newCategoryName = categoryDTO.getName();
            Double newBasePrice = categoryDTO.getBasePrice();
            String newDescription = categoryDTO.getDescription();
            Category updatedCategory = Category.builder().
                    categoryName(newCategoryName).
                    basePrice(newBasePrice).
                    description(newDescription).build();
            updatedCategory.setId(categoryId);
            // check to see if the new category name is not duplicate!
            if (existsByCategoryName(newCategoryName) && !findByCategoryName(newCategoryName).getId().equals(categoryId))
                throw new InvalidInputException("duplicate category names are not allowed!");
            else {
                // check to see if category is going to be a parent
                if (categoryDTO.getParentCategoryId() == null) {
                    if (newBasePrice != null || newDescription != null)
                        throw new InputMismatchException("only sub-categories can take base price or description!");
                    Set<ConstraintViolation<Category>> violations = ApplicationContext.getValidator().validate(updatedCategory);
                    if (!violations.isEmpty())
                        throw new ConstraintViolationException(violations);
                    repository.save(updatedCategory);
                    // category is not parent check to see if the parent category is valid and can be fetched
                } else if (
                        !repository.existsById(categoryDTO.getParentCategoryId()) ||
                                categoryId.equals(categoryDTO.getParentCategoryId())
                ) {
                    throw new InvalidInputException("Parent ID does not exist or it is the same as that of the current category");
                } else {
                    Category newParentCategory = repository.findById(categoryDTO.getParentCategoryId()).
                            orElseThrow(() -> new ExistingEntityCannotBeFetchedException("Parent category exists but cannot be fetched!"));
                    updatedCategory.setParentCategory(newParentCategory);
                    if (newBasePrice == null || newDescription.isBlank())
                        throw new InputMismatchException("sub-categories must have a description and a base price!");
                    Set<ConstraintViolation<Category>> violations = ApplicationContext.getValidator().validate(updatedCategory);
                    if (!violations.isEmpty())
                        throw new ConstraintViolationException(violations);
                    repository.save(updatedCategory);
                }
            }
        } finally {
            SemaphoreUtil.releaseNewCategorySemaphore();
        }
    }

    @Transactional
    @Override
    public List<FoundCategoryDTO> findByTradesManId(Long tradesManId) {
        if (!repository.tradesmanExistsById(tradesManId))
            throw new InvalidInputException("TradesMan could not be found!");
        List<Category> categoryList = repository.findByTradesManId(tradesManId);
        List<FoundCategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category c : categoryList)
            categoryDTOS.add(mapToDTO(c));
        return categoryDTOS;
    }

    @Override
    public void addTradesManToSubCategory(Long tradesManId, Long categoryId) throws InterruptedException {
        SemaphoreUtil.acquireNewCategorySemaphore();
        try {
            Category category = repository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category does not exist or could not be fetched!"));
            if (category.getParentCategory() == null)
                throw new InputMismatchException("Only subcategories can have tradesmen! this is a parent category!");
            TradesMan tradesMan = repository.findTradesManByTradesManId(tradesManId).orElseThrow(() -> new IllegalArgumentException("the TradesMan does not exist or could not be fetched!"));
            if (!tradesMan.isActive() || !tradesMan.getStatus().equals(TradesManStatus.APPROVED))
                throw new IllegalArgumentException("The tradesMan exists but is not active/approved!");
            Set<TradesMan> tradesMen;
            tradesMen = category.getTradesMen();
            if (tradesMen != null && !tradesMen.isEmpty()) {
                for (TradesMan t : tradesMen) {
                    if (t.getId().equals(tradesManId))
                        throw new IllegalArgumentException("Tradesman already exists in this subcategory!");
                }
            }
            category.getTradesMen().add(tradesMan);
            repository.save(category);
        } finally {
            SemaphoreUtil.releaseNewCategorySemaphore();
        }
    }

    @Transactional
    @Override
    public void removeTradesManFromSubCategory(Long tradesManId, Long categoryId) throws InterruptedException {
        SemaphoreUtil.acquireNewCategorySemaphore();
        try {
            Category category = repository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("category does not exist or could not be fetched!"));
            if (!repository.tradesmanExistsById(tradesManId))
                throw new IllegalArgumentException("Tradesman does not exist!");
            Set<TradesMan> tradesMen;
            tradesMen = category.getTradesMen();
            if (tradesMen == null || tradesMen.isEmpty())
                throw new NoSuchElementException("Category does not have any tradesmen!");
            category.getTradesMen().removeIf(tradesMan -> tradesMan.getId().equals(tradesManId));
            repository.save(category);
        } finally {
            SemaphoreUtil.releaseNewCategorySemaphore();
        }
    }

    @Transactional
    public void deleteById(Long categoryId) {
        if (!repository.existsById(categoryId))
            throw new IllegalArgumentException("Category does not exist!");

        repository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public FoundCategoryDTO mapToDTO(Category foundCategory) {
        FoundCategoryDTO foundCategoryDTO = new FoundCategoryDTO();
        foundCategoryDTO.setName(foundCategory.getCategoryName());
        foundCategoryDTO.setId(foundCategory.getId());
        boolean isParent = isParent(foundCategory.getId());
        if (!isParent) {
            foundCategoryDTO.setBasePrice(foundCategory.getBasePrice());
            foundCategoryDTO.setParentCategoryId(foundCategory.getParentCategory().getId());
            foundCategoryDTO.setDescription(foundCategory.getDescription());
            Set<TradesMan> tradesMen = foundCategory.getTradesMen();
            if (tradesMen != null) {
                for (TradesMan t : tradesMen)
                    foundCategoryDTO.getTradesManIds().add(t.getId());
            }
        }
        return foundCategoryDTO;
    }
}
