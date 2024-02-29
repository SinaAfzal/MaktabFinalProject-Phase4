package ir.maktabsharif.controller;

import io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2PushPromiseFrame;
import ir.maktabsharif.model.Customer;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.service.ProposalService;
import ir.maktabsharif.service.TaskService;
import ir.maktabsharif.service.TradesManService;
import ir.maktabsharif.service.dto.request.TaskProposalDTO;
import ir.maktabsharif.service.dto.request.TradesManRegistrationDTO;
import ir.maktabsharif.service.dto.request.UserChangePasswordDTO;
import ir.maktabsharif.service.dto.request.UserEditProfileDTO;
import ir.maktabsharif.service.dto.response.FoundProposalDTO;
import ir.maktabsharif.service.dto.response.FoundTaskDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import ir.maktabsharif.util.exception.AccessDeniedException;
import ir.maktabsharif.util.exception.InvalidInputException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping("/tradesman")
public class TradesManController {
    private final TradesManService tradesManService;
    private final ProposalService proposalService;
    private final TaskService taskService;

    public TradesManController(TradesManService tradesManService, ProposalService proposalService, TaskService taskService) {
        this.tradesManService = tradesManService;
        this.proposalService = proposalService;
        this.taskService = taskService;
    }

    @GetMapping("/exists-by-email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = tradesManService.existsByEmail(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseEntity<ResponseDTO> findByEmail(@PathVariable String email) {
        ResponseDTO admin = tradesManService.findTradesManByEmail(email);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }


    @DeleteMapping("/delete-tradesman")
    public ResponseEntity<Void> deleteTradesManById() {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tId = principal.getId();
        tradesManService.deleteTradesManById(tId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidInputException {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getEmail();
        tradesManService.changePassword(email, userChangePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<Void> editProfile(@Valid @RequestBody UserEditProfileDTO userEditProfileDTO) throws InterruptedException, InvalidInputException, AccessDeniedException {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tId = principal.getId();
        tradesManService.editProfile(tId, userEditProfileDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find-proposals-of-tradesman")
    public ResponseEntity<List<FoundProposalDTO>> findProposalByTradesmanId() {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tId = principal.getId();
        List<FoundProposalDTO> dtoList = proposalService.findByTradesManId(tId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PostMapping("/create-proposal")
    public ResponseEntity<Void> createProposal(@RequestBody @Valid TaskProposalDTO taskProposalDTO) {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tradesmanId = principal.getId();
        proposalService.createProposal(tradesmanId, taskProposalDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find-task-by-status-and-tradesman-category")
    public ResponseEntity<List<FoundTaskDTO>> findTaskByStatusAndTradesManCategory(@RequestParam TaskStatus status) {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tradesmanId = principal.getId();
        List<FoundTaskDTO> taskDTOs = taskService.findTasksByStatusAndTradesManCategories(status, tradesmanId);
        return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
    }

    @GetMapping("/view-task-rating")
    public ResponseEntity<Float> viewTaskRating(@RequestParam Long taskId) {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tradesmanId = principal.getId();
        Float rating = taskService.viewRatingOfDoneTask(taskId, tradesmanId);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }

    @GetMapping("/find-task-by-tradesman-and-task-status")
    public ResponseEntity<List<FoundTaskDTO>> findTasksByTradesManAndStatus(@RequestParam TaskStatus status) {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tradesmanId = principal.getId();
        List<FoundTaskDTO> dtoList = taskService.findTasksByTradesManAndStatus(tradesmanId, status);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @DeleteMapping("/delete-proposal")
    public ResponseEntity<Void> deleteProposalById(@RequestParam Long pId) {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tradesmanId = principal.getId();
        proposalService.deleteProposalById(pId, tradesmanId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-earned-credit")
    public ResponseEntity<Double> getBalance() {
        TradesMan principal = (TradesMan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = principal.getId();
        return new ResponseEntity<>(tradesManService.getEarnedCredit(id), HttpStatus.OK);
    }
}
