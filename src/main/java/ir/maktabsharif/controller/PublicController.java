package ir.maktabsharif.controller;

import ir.maktabsharif.service.*;
import ir.maktabsharif.service.dto.request.AdminRegistrationDTO;
import ir.maktabsharif.service.dto.request.CustomerRegistrationDTO;
import ir.maktabsharif.service.dto.request.TradesManRegistrationDTO;
import ir.maktabsharif.service.dto.response.FoundCategoryDTO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Controller
@RequestMapping("/public-context")
public class PublicController {
    private final CategoryService categoryService;
    private final TradesManService tradesManService;
    private final AdminService adminService;
    private final CustomerService customerService;
    private final UserService userService;

    public PublicController(CategoryService categoryService, TradesManService tradesManService, AdminService adminService, CustomerService customerService, UserService userService) {
        this.categoryService = categoryService;
        this.tradesManService = tradesManService;
        this.adminService = adminService;
        this.customerService = customerService;
        this.userService = userService;
    }

    @GetMapping("/find-category-by-name")
    @ResponseBody
    public ResponseEntity<FoundCategoryDTO> findByCategoryName(@RequestParam String categoryName) {
        FoundCategoryDTO dto = categoryService.findByCategoryNameIgnoreCase(categoryName);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/find-subcategories")
    @ResponseBody
    public ResponseEntity<List<FoundCategoryDTO>> findSubCategories(@RequestParam Long cId) {
        List<FoundCategoryDTO> subCategories = categoryService.findSubCategories(cId);
        return new ResponseEntity<>(subCategories, HttpStatus.OK);
    }

    @GetMapping("/all-parent-categories")
    @ResponseBody
    public ResponseEntity<List<FoundCategoryDTO>> findAllParentCategories() {
        List<FoundCategoryDTO> allParentCategories = categoryService.findAllParentCategories();
        return new ResponseEntity<>(allParentCategories, HttpStatus.OK);
    }

    @GetMapping("/categories-of-tradesman")
    @ResponseBody
    public ResponseEntity<List<FoundCategoryDTO>> findCategoriesOfTradesman(@RequestParam Long tId) {
        List<FoundCategoryDTO> dtoList = categoryService.findByTradesManId(tId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PostMapping("/download-tradesman-avatar")
    @ResponseBody
    public ResponseEntity<Void> downloadAvatar(@RequestParam Long tId, @RequestParam String savePath) throws IOException {
        tradesManService.downloadTradesManAvatar(tId, savePath);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin-register")
    @ResponseBody
    public ResponseEntity<Void> register(@RequestBody AdminRegistrationDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException, MessagingException {
        adminService.register(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/customer-register")
    @ResponseBody
    public ResponseEntity<Void> register(@RequestBody @Valid CustomerRegistrationDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException, MessagingException {
        customerService.register(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tradesman-register")
    @ResponseBody
    public ResponseEntity<Void> register(@RequestBody @Valid TradesManRegistrationDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException, IOException, MessagingException {
        tradesManService.register(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping ("/verify-email")
    public RedirectView verifyEmail(@RequestParam String tokenSerial) {
        userService.verifyEmailByTokenAndEnableUser(tokenSerial);
        return new RedirectView("/public-context/email-verification-success");
    }

    @GetMapping(value = "/email-verification-success",produces = "text/html")
    public String getEmailVerificationSuccessPage() {
        return "email-verif-success";
    }

    @GetMapping("/request-email-verification")
    @ResponseBody
    public ResponseEntity<Void> requestEmailVerification(@RequestParam String email) throws MessagingException {
        userService.sendEmailVerificationEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
