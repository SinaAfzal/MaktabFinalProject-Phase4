package ir.maktabsharif.controller;

import ir.maktabsharif.service.CategoryService;
import ir.maktabsharif.service.TradesManService;
import ir.maktabsharif.service.dto.response.FoundCategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/public-context")
public class PublicController {
    private final CategoryService categoryService;
    private final TradesManService tradesManService;

    public PublicController(CategoryService categoryService, TradesManService tradesManService) {
        this.categoryService = categoryService;
        this.tradesManService = tradesManService;
    }

    @GetMapping("/find-category-by-name")
    public ResponseEntity<FoundCategoryDTO> findByCategoryName(@RequestParam String categoryName){
        FoundCategoryDTO dto = categoryService.findByCategoryNameIgnoreCase(categoryName);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/find-subcategories")
    public ResponseEntity<List<FoundCategoryDTO>> findSubCategories(@RequestParam Long cId){
        List<FoundCategoryDTO> subCategories = categoryService.findSubCategories(cId);
        return new ResponseEntity<>(subCategories,HttpStatus.OK);
    }

    @GetMapping("/all-parent-categories")
    public ResponseEntity<List<FoundCategoryDTO>> findAllParentCategories(){
        List<FoundCategoryDTO> allParentCategories = categoryService.findAllParentCategories();
        return new ResponseEntity<>(allParentCategories,HttpStatus.OK);
    }

    @GetMapping("/categories-of-tradesman")
    public ResponseEntity<List<FoundCategoryDTO>> findCategoriesOfTradesman(@RequestParam Long tId){
        List<FoundCategoryDTO> dtoList = categoryService.findByTradesManId(tId);
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }

    @PostMapping("/download-tradesman-avatar")
    public ResponseEntity<Void> downloadAvatar(@RequestParam Long tId,@RequestParam String savePath) throws IOException {
        tradesManService.downloadTradesManAvatar(tId,savePath);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
