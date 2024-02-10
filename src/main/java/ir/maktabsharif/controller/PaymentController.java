package ir.maktabsharif.controller;

import ir.maktabsharif.model.Proposal;
import ir.maktabsharif.model.recaptcha.RecaptchaResponse;

import ir.maktabsharif.service.TaskService;
import ir.maktabsharif.service.dto.request.PaymentDTO;
import ir.maktabsharif.util.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.naming.TimeLimitExceededException;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class PaymentController {
    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String RECAPTCHA_SECRET = "6Le1ZGUpAAAAAPXq4qCQ5Sedz5_6u-GwjvcP-1br";


    private final TaskService taskService;
    private LocalDateTime enteringToPageTime;

    public PaymentController(TaskService taskService) {
        this.taskService = taskService;

    }

    @GetMapping("/payment")
    public String showPaymentForm(Model model, HttpSession session, @RequestParam Long taskId, @RequestParam Long customerId) {
        enteringToPageTime = LocalDateTime.now();
        model.addAttribute("payment", new PaymentDTO());
        session.setAttribute("taskId", taskId);
        session.setAttribute("customerId", customerId);
        double price = 0D;
        Optional<Proposal> winnerProposal = taskService.findWinnerProposal(taskId);
        if (winnerProposal.isPresent()) {
            price = winnerProposal.get().getProposedPrice();
        }
        session.setAttribute("price", price);
        return "/payment-folder/payment-form";
    }

    @PostMapping("/payment")
    public String processPayment(@Valid @ModelAttribute("payment") PaymentDTO paymentDTO, BindingResult result, HttpServletRequest request, HttpSession session) throws TimeLimitExceededException {
        if (LocalDateTime.now().isAfter(enteringToPageTime.plusSeconds(600L)))
            throw new TimeLimitExceededException("Time is out! please re-initiate the payment process!");
        String recaptchaResponse = request.getParameter("g-recaptcha-response");
        boolean isRecaptchaValid = verifyRecaptcha(recaptchaResponse);
        if (!isRecaptchaValid) {
            result.rejectValue("recaptcha", "error.recaptcha", "Please complete the reCAPTCHA");
        }
        if (result.hasErrors()) {
            return "/payment-folder/payment-form";
        }
        Long taskId = (Long) session.getAttribute("taskId");
        Long customerId = (Long) session.getAttribute("customerId");
        taskService.payTaskUsingBankAccount(taskId, customerId);
        return "redirect:/payment/success";
    }

    @GetMapping("payment/success")
    public String showSuccessfulPaymentPage() {
        return "/payment-folder/success-page";
    }

    private boolean verifyRecaptcha(String recaptchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        String url = RECAPTCHA_URL + "?secret=" + RECAPTCHA_SECRET + "&response=" + recaptchaResponse;
        RecaptchaResponse response = restTemplate.postForObject(url, null, RecaptchaResponse.class);
        if (response == null)
            throw new NullPointerException("Please complete the reCAPTCHA!");
        return response.isSuccess();
    }
}
