package ir.maktabsharif.controller;

import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.service.CustomerService;
import ir.maktabsharif.service.ProposalService;
import ir.maktabsharif.service.TaskService;
import ir.maktabsharif.service.dto.request.*;
import ir.maktabsharif.service.dto.response.FoundProposalDTO;
import ir.maktabsharif.service.dto.response.FoundTaskDTO;
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
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;
    private final ProposalService proposalService;
    private final TaskService taskService;

    public CustomerController(CustomerService customerService, ProposalService proposalService, TaskService taskService) {
        this.customerService = customerService;
        this.proposalService = proposalService;
        this.taskService = taskService;
    }

    @GetMapping("/exists-by-email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = customerService.existsByEmail(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseEntity<ResponseDTO> findByEmail(@PathVariable String email) {
        ResponseDTO admin = customerService.findByEmail(email);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid CustomerRegistrationDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        customerService.register(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-customer/{cId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable Long cId) {
        customerService.deleteCustomerById(cId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/change-password/{email}")
    public ResponseEntity<Void> changePassword(@PathVariable String email, @Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidInputException {
        customerService.changePassword(email, userChangePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit-profile/{id}")
    public ResponseEntity<Void> editProfile(@PathVariable Long id, @Valid @RequestBody UserEditProfileDTO userEditProfileDTO) throws InterruptedException, InvalidInputException, AccessDeniedException {
        customerService.editProfile(id, userEditProfileDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find-proposals-by-taskId")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskId(@RequestParam Long taskId) {
        List<FoundProposalDTO> dtoList = proposalService.findByTaskId(taskId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/find-proposals-of-task-sort-by-price")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskIdSortByProposedPriceAscending(@RequestParam Long taskId) {
        List<FoundProposalDTO> dtoList = proposalService.findProposalsByTaskIdSortByProposedPriceAscending(taskId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/find-proposals-of-task-sort-by-tradesman-rating")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskIdSortByTradesManScoreAscending(@RequestParam Long taskId) {
        List<FoundProposalDTO> dtoList = proposalService.findProposalsByTaskIdSortByTradesManScoreAscending(taskId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }


    @PostMapping("/request-task")
    public ResponseEntity<Void> requestTask(@RequestBody TaskRequestDTO taskRequestDTO, @RequestParam Long customerId) {
        taskService.requestTask(taskRequestDTO, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit-task-request")
    public ResponseEntity<Void> editTaskRequest(@RequestParam Long taskId, @RequestBody TaskRequestDTO taskRequestDTO, @RequestParam Long customerId) {
        taskService.editTaskRequest(taskId, taskRequestDTO, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/pay-task-with-credit")
    public ResponseEntity<Void> payTaskWithCredit(@RequestParam Long taskId, @RequestParam Long customerId) {
        taskService.payTaskFromCustomerCredit(taskId, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/comment-and-rate-task")
    public ResponseEntity<Void> CommentAndRateTask(@RequestBody @Valid TaskRatingDTO taskRatingDTO, @RequestParam Long customerId) {
        taskService.addCommentAndRate(taskRatingDTO, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/change-task-status")
    public ResponseEntity<Void> changeTaskStatus(@RequestParam Long taskId, @RequestParam TaskStatus status, @RequestParam Long customerId) {
        taskService.changeTaskStatusByCustomer(taskId, status, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-task")
    public ResponseEntity<Void> deleteTask(@RequestParam Long taskId, @RequestParam Long customerId) {
        taskService.deleteTaskById(taskId, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/select-proposal")
    public ResponseEntity<Void> selectProposal(@RequestBody SelectTradesManProposalDTO dto, @RequestParam Long customerId) {
        taskService.selectTradesManProposal(dto, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find-task-by-customer-and-status")
    public ResponseEntity<List<FoundTaskDTO>> findTaskByCustomerAndStatus(@RequestParam Long customerId, @RequestParam TaskStatus status) {
        List<FoundTaskDTO> dtoList = taskService.findTasksByCustomerAndStatus(customerId, status);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }
}
