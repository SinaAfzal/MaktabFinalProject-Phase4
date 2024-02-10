package ir.maktabsharif.controller;

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
        ResponseDTO admin = tradesManService.findByEmail(email);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid TradesManRegistrationDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException, IOException {
        tradesManService.register(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-tradesman/{tId}")
    public ResponseEntity<Void> deleteTradesManById(@PathVariable Long tId) {
        tradesManService.deleteTradesManById(tId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping("/change-password/{email}")
    public ResponseEntity<Void> changePassword(@PathVariable String email, @Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidInputException {
        tradesManService.changePassword(email, userChangePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit-profile/{id}")
    public ResponseEntity<Void> editProfile(@PathVariable Long id, @Valid @RequestBody UserEditProfileDTO userEditProfileDTO) throws InterruptedException, InvalidInputException, AccessDeniedException {
        tradesManService.editProfile(id, userEditProfileDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find-proposals-by-tradesman-id")
    public ResponseEntity<List<FoundProposalDTO>> findProposalByTradesmanId(@RequestParam Long tId) {
        List<FoundProposalDTO> dtoList = proposalService.findByTradesManId(tId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PostMapping("/create-proposal")
    public ResponseEntity<Void> createProposal(@RequestParam Long tradesmanId, @RequestBody TaskProposalDTO taskProposalDTO) {
        proposalService.createProposal(tradesmanId, taskProposalDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find-task-by-status-and-tradesman-category")
    public ResponseEntity<List<FoundTaskDTO>> findTaskByStatusAndTradesManCategory(@RequestParam TaskStatus status, @RequestParam Long tradesmanId) {
        List<FoundTaskDTO> taskDTOs = taskService.findTasksByStatusAndTradesManCategories(status, tradesmanId);
        return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
    }

    @GetMapping("/view-task-rating")
    public ResponseEntity<Float> viewTaskRating(@RequestParam Long taskId,@RequestParam Long tradesmanId){
        Float rating = taskService.viewRatingOfDoneTask(taskId, tradesmanId);
        return new ResponseEntity<>(rating,HttpStatus.OK);
    }
    @GetMapping("/find-task-by-tradesman-and-task-status")
    public ResponseEntity<List<FoundTaskDTO>> findTasksByTradesManAndStatus(@RequestParam Long tradesmanId,@RequestParam TaskStatus status){
        List<FoundTaskDTO> dtoList = taskService.findTasksByTradesManAndStatus(tradesmanId, status);
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }
    @DeleteMapping("/delete-proposal")
    public ResponseEntity<Void> deleteProposalById(@RequestParam Long pId,@RequestParam Long tId){
        proposalService.deleteProposalById(pId,tId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
