package ir.maktabsharif.controller;

import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.service.*;
import ir.maktabsharif.service.dto.request.*;
import ir.maktabsharif.service.dto.response.FoundProposalDTO;
import ir.maktabsharif.service.dto.response.FoundTaskDTO;
import ir.maktabsharif.service.dto.response.FoundTradesManDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import ir.maktabsharif.util.exception.AccessDeniedException;
import ir.maktabsharif.util.exception.InvalidInputException;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final CategoryService categoryService;
    private final TradesManService tradesManService;
    private final CustomerService customerService;
    private final ProposalService proposalService;
    private final TaskService taskService;

    public AdminController(AdminService adminService, CategoryService categoryService, TradesManService tradesManService, CustomerService customerService, ProposalService proposalService, TaskService taskService) {
        this.adminService = adminService;
        this.categoryService = categoryService;
        this.tradesManService = tradesManService;
        this.customerService = customerService;
        this.proposalService = proposalService;
        this.taskService = taskService;
    }

    @GetMapping("/exists-by-email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = adminService.existsByEmail(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseEntity<ResponseDTO> findByEmail(@PathVariable String email){
        ResponseDTO admin = adminService.findByEmail(email);
        return new ResponseEntity<>(admin,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AdminRegistrationDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        adminService.register(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/change-password/{email}")
    public ResponseEntity<Void> changePassword(@PathVariable String email,@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidInputException {
        adminService.changePassword(email,userChangePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit-profile/{id}")
    public ResponseEntity<Void> editProfile(@PathVariable Long id,@Valid @RequestBody UserEditProfileDTO userEditProfileDTO) throws InterruptedException, InvalidInputException, AccessDeniedException {
        adminService.editProfile(id,userEditProfileDTO);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/find-users")//todo implement pagination and wrapping of list elements based on their type
    public ResponseEntity<List<BaseUser>> findUsers(@RequestBody AdvancedUserSearchDTO dto){
        List<BaseUser> baseUsers = adminService.advancedUserSearch(dto);
        return new ResponseEntity<>(baseUsers,HttpStatus.OK);
    }

    @PostMapping("/add-category")
    public ResponseEntity<Void> addCategory(@RequestBody @Valid CategoryDTO categoryDTO) throws InterruptedException {
        categoryService.addCategory(categoryDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit-category")
    public ResponseEntity<Void> editCategory(@RequestParam Long cId,@RequestBody @Valid CategoryDTO categoryDTO) throws InterruptedException {
        categoryService.editCategory(cId,categoryDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/assign-tradesman-to-subcategory")
    public ResponseEntity<Void> assignTradesManToSubcategory(@RequestParam Long tId,@RequestParam Long cId) throws InterruptedException {
        categoryService.addTradesManToSubCategory(tId,cId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/remove-tradesman-from-subcategory")
    public ResponseEntity<Void> removeTradesManToSubcategory(@RequestParam Long tId,@RequestParam Long cId) throws InterruptedException {
        categoryService.removeTradesManFromSubCategory(tId,cId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-category")
    public ResponseEntity<Void> deleteCategory(@RequestParam Long cId){
        categoryService.deleteById(cId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/change-tradesman-status")
    public ResponseEntity<Void> changeStatus(@RequestParam Long tId,@RequestParam TradesManStatus status){
        tradesManService.changeTradesManStatus(tId,status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-customer/{cId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable Long cId) {
        customerService.deleteCustomerById(cId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/delete-tradesman/{tId}")
    public ResponseEntity<Void> deleteTradesManById(@PathVariable Long tId){
        tradesManService.deleteTradesManById(tId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/find-tradesman-by-status")
    public ResponseEntity<List<FoundTradesManDTO>> findTradesMenByStatus(@RequestParam TradesManStatus status){
        List<FoundTradesManDTO> tradesMenByStatus = tradesManService.findTradesMenByStatus(status);
        return new ResponseEntity<>(tradesMenByStatus,HttpStatus.OK);
    }

    @GetMapping("/find-proposals-by-tradesman-id")
    public ResponseEntity<List<FoundProposalDTO>> findProposalByTradesmanId(@RequestParam Long tId){
        List<FoundProposalDTO> dtoList = proposalService.findByTradesManId(tId);
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }
    @GetMapping("/find-proposals-by-taskId")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskId(@RequestParam Long taskId){
        List<FoundProposalDTO> dtoList = proposalService.findByTaskId(taskId);
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }
    @GetMapping("/find-proposals-of-task-sort-by-price")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskIdSortByProposedPriceAscending(@RequestParam Long taskId){
        List<FoundProposalDTO> dtoList = proposalService.findProposalsByTaskIdSortByProposedPriceAscending(taskId);
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }
    @GetMapping("/find-proposals-of-task-sort-by-tradesman-rating")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskIdSortByTradesManScoreAscending(@RequestParam Long taskId){
        List<FoundProposalDTO> dtoList = proposalService.findProposalsByTaskIdSortByTradesManScoreAscending(taskId);
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }

    @GetMapping("/find-task-by-status")
    public ResponseEntity<List<FoundTaskDTO>> findTaskByStatus(@RequestParam TaskStatus status) {
        List<FoundTaskDTO> taskDTOs = taskService.findTasksByStatus(status);
        return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
    }
    @GetMapping("/find-task-by-tradesman-and-task-status")
    public ResponseEntity<List<FoundTaskDTO>> findTasksByTradesManAndStatus(@RequestParam Long tradesmanId,@RequestParam TaskStatus status){
        List<FoundTaskDTO> dtoList = taskService.findTasksByTradesManAndStatus(tradesmanId, status);
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }
    @GetMapping("/find-task-by-customer-and-task-status")
    public ResponseEntity<List<FoundTaskDTO>> findTaskByCustomerAndStatus(@RequestParam Long customerId, @RequestParam TaskStatus status) {
        List<FoundTaskDTO> dtoList = taskService.findTasksByCustomerAndStatus(customerId, status);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

}
