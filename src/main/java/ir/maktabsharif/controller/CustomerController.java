package ir.maktabsharif.controller;

import ir.maktabsharif.model.Customer;
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
import org.hibernate.engine.spi.Resolution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
        ResponseDTO admin = customerService.findCustomerByEmail(email);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }



    @DeleteMapping("/delete-customer")
    public ResponseEntity<Void> deleteCustomerById() {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long cId = principal.getId();
        customerService.deleteCustomerById(cId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidInputException {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getEmail();
        customerService.changePassword(email, userChangePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<Void> editProfile(@Valid @RequestBody UserEditProfileDTO userEditProfileDTO) throws InterruptedException, InvalidInputException, AccessDeniedException {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long cId = principal.getId();
        customerService.editProfile(cId, userEditProfileDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find-proposals-by-taskId")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskId(@RequestParam Long taskId) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long cId = principal.getId();
        List<FoundProposalDTO> dtoList = proposalService.findByTaskId(taskId,cId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/find-proposals-of-task-sort-by-price")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskIdSortByProposedPriceAscending(@RequestParam Long taskId) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long cId = principal.getId();
        List<FoundProposalDTO> dtoList = proposalService.findProposalsByTaskIdSortByProposedPriceAscending(taskId,cId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/find-proposals-of-task-sort-by-tradesman-rating")
    public ResponseEntity<List<FoundProposalDTO>> findProposalsByTaskIdSortByTradesManScoreAscending(@RequestParam Long taskId) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long cId = principal.getId();
        List<FoundProposalDTO> dtoList = proposalService.findProposalsByTaskIdSortByTradesManScoreAscending(taskId,cId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }


    @PostMapping("/request-task")
    public ResponseEntity<Void> requestTask(@RequestBody TaskRequestDTO taskRequestDTO) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long cId = principal.getId();
        taskService.requestTask(taskRequestDTO, cId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit-task-request")
    public ResponseEntity<Void> editTaskRequest(@RequestParam Long taskId, @RequestBody TaskRequestDTO taskRequestDTO) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long customerId = principal.getId();
        taskService.editTaskRequest(taskId, taskRequestDTO, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/pay-task-with-credit")
    public ResponseEntity<Void> payTaskWithCredit(@RequestParam Long taskId) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long customerId = principal.getId();
        taskService.payTaskFromCustomerCredit(taskId, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/comment-and-rate-task")
    public ResponseEntity<Void> CommentAndRateTask(@RequestBody @Valid TaskRatingDTO taskRatingDTO) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long customerId = principal.getId();
        taskService.addCommentAndRate(taskRatingDTO, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/change-task-status")
    public ResponseEntity<Void> changeTaskStatus(@RequestParam Long taskId, @RequestParam TaskStatus status) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long customerId = principal.getId();
        taskService.changeTaskStatusByCustomer(taskId, status, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-task")
    public ResponseEntity<Void> deleteTask(@RequestParam Long taskId) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long customerId = principal.getId();
        taskService.deleteTaskById(taskId, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/select-proposal")
    public ResponseEntity<Void> selectProposal(@RequestBody SelectTradesManProposalDTO dto) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long customerId = principal.getId();
        taskService.selectTradesManProposal(dto, customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find-task-by-customer-and-status")
    public ResponseEntity<List<FoundTaskDTO>> findTaskByCustomerAndStatus(@RequestParam TaskStatus status) {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long customerId = principal.getId();
        List<FoundTaskDTO> dtoList = taskService.findTasksByCustomerAndStatus(customerId, status);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/get-purchased-balance")
     public ResponseEntity<Double> getBalance(){
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = principal.getId();
        return new ResponseEntity<>(customerService.getPurchasedCredit(id),HttpStatus.OK);
    }
}
