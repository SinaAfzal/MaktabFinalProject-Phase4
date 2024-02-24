package ir.maktabsharif.service.impl;


import ir.maktabsharif.model.Category;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.repository.CategoryRepository;
import ir.maktabsharif.service.CategoryService;
import ir.maktabsharif.service.dto.request.CategoryDTO;
import ir.maktabsharif.service.dto.response.FoundCategoryDTO;
import ir.maktabsharif.util.SemaphoreUtil;
import ir.maktabsharif.util.exception.ExistingEntityCannotBeFetchedException;
import ir.maktabsharif.util.exception.InvalidInputException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
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
            if (existsByCategoryName(categoryDTO.getName()))
                throw new InvalidInputException("Duplicate category names are not allowed!");
            Category categoryToBeSaved = Category.builder()
                    .categoryName(categoryDTO.getName())
                    .build();
            if (categoryDTO.getParentCategoryId() == null) {//category to be saved is a parent-category
                if (categoryDTO.getBasePrice() != null || categoryDTO.getDescription() != null)
                    throw new InputMismatchException("Only sub-categories can take base price or description!");
            } else {// category to be saved is a sub-category
                if (!repository.existsById(categoryDTO.getParentCategoryId()))
                    throw new InvalidInputException("Parent Id is not valid");
                if (categoryDTO.getBasePrice() == null || categoryDTO.getDescription().isBlank())
                    throw new InputMismatchException("sub-categories must have a basePrice and description!");
                categoryToBeSaved.setBasePrice(categoryDTO.getBasePrice());
                categoryToBeSaved.setDescription(categoryDTO.getDescription());
                categoryToBeSaved.setParentCategory(repository.findById(categoryDTO.getParentCategoryId()).orElseThrow(() -> new ExistingEntityCannotBeFetchedException("Parent category exists but cannot be fetched!")));
            }
            repository.save(categoryToBeSaved);
        } finally {
            SemaphoreUtil.releaseNewCategorySemaphore();
        }
    }

    @Transactional
    @Override
    public void editCategory(Long categoryId, CategoryDTO categoryDTO) throws InterruptedException {
        SemaphoreUtil.acquireNewCategorySemaphore();
        try {
            if (!repository.existsById(categoryId))
                throw new InvalidInputException("Invalid category ID!");
            Category category=repository.findById(categoryId).orElseThrow(()->new ExistingEntityCannotBeFetchedException("Category could not be fetched!"));
            Category updatedCategory = Category.builder().
                    categoryName(categoryDTO.getName()).
                    basePrice(categoryDTO.getBasePrice()).
                    description(categoryDTO.getDescription()).build();
            updatedCategory.setId(categoryId);
            if (existsByCategoryName(categoryDTO.getName()) && !findByCategoryName(categoryDTO.getName()).getId().equals(categoryId))
                throw new InvalidInputException("duplicate category names are not allowed!");
            if (categoryDTO.getParentCategoryId() == null) {//category is going to be a parent
                if (category.getParentCategory()!=null)
                    throw new InputMismatchException("A sub-category cannot be converted to a parent category!");
                if (categoryDTO.getBasePrice() != null || categoryDTO.getDescription() != null)
                    throw new InputMismatchException("only sub-categories can take base price or description!");
            } else {//category is going to become a subcategory
                if (category.getParentCategory()==null)
                    throw new InputMismatchException("A parent category cannot be converted to a sub-category!");
                if (!repository.existsById(categoryDTO.getParentCategoryId()) || categoryId.equals(categoryDTO.getParentCategoryId()))
                    throw new InvalidInputException("Parent ID does not exist or it is the same as that of the current category");
                updatedCategory.setParentCategory(repository.findById(categoryDTO.getParentCategoryId()).orElseThrow(() -> new ExistingEntityCannotBeFetchedException("Parent category exists but cannot be fetched!")));
                if (categoryDTO.getBasePrice() == null || categoryDTO.getDescription().isBlank())
                    throw new InputMismatchException("sub-categories must have a description and a base price!");
            }
            repository.save(updatedCategory);
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
    @Transactional
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
