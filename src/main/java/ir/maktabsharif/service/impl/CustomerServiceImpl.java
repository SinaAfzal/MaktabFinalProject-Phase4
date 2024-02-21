package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.Customer;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.repository.CustomerRepository;
import ir.maktabsharif.repository.UserRepository;
import ir.maktabsharif.service.*;
import ir.maktabsharif.service.dto.request.CustomerRegistrationDTO;
import ir.maktabsharif.service.dto.response.FoundCustomerDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import ir.maktabsharif.util.*;
import ir.maktabsharif.util.exception.ExistingEntityCannotBeFetchedException;
import ir.maktabsharif.util.exception.InvalidInputException;

import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl extends
        UserServiceImpl implements
        CustomerService {
    private final TaskService taskService;
    private final CustomerRepository repository;

    public CustomerServiceImpl(UserRepository userRepository, EmailConfirmationTokenService emailConfirmationTokenService, EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder, TaskService taskService, CustomerRepository repository) {
        super(userRepository, emailConfirmationTokenService, emailService, bCryptPasswordEncoder);
        this.taskService = taskService;
        this.repository = repository;
    }


    @Override
    @Transactional
    public void register(CustomerRegistrationDTO cuReDTO) throws InterruptedException, MessagingException {
        SemaphoreUtil.acquireNewUserSemaphore();
        try {
            String email = cuReDTO.getEmail();
            String firstName = cuReDTO.getFirstName();
            String lastName = cuReDTO.getLastName();
            String notHashedPassword = cuReDTO.getNotHashedPassword();

            if (!Validation.isEmailValid(email))
                throw new InvalidInputException("Email pattern is not valid!");

            if (existsByEmail(email))
                throw new InvalidInputException("Email already exists on database!");

            if (!Validation.isPasswordValid(notHashedPassword))
                throw new InvalidInputException("Password is not Strong enough!");

            //hash password:
            String hashedPassword = bCryptPasswordEncoder.encode(notHashedPassword);

            Customer customer = new Customer();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setPassword(hashedPassword);
            customer.setRole(UserRole.ROLE_CUSTOMER);
            customer.setRegistrationDateTime(LocalDateTime.now());
            customer.setPurchasedBalance(0.00D);
            customer.setActive(true);
            Set<ConstraintViolation<Customer>> violations = ApplicationContext.getValidator().validate(customer);
            if (!violations.isEmpty())
                throw new ConstraintViolationException(violations);
            repository.save(customer);
            sendEmailVerificationEmail(email);
        } finally {
            SemaphoreUtil.releaseNewUserSemaphore();
        }
    }

    @Override
    @Transactional
    public void deleteCustomerById(Long customerId) {
        if (!repository.existsById(customerId))
            throw new IllegalArgumentException("Invalid customer ID!");
        Customer customer = repository.findCustomerById(customerId).orElseThrow(() -> new ExistingEntityCannotBeFetchedException("Customer exists but cannot be fetched!"));
        if (taskService.findTasksByCustomerAndStatus_NoLogic_DirectlyCallingRepo(customerId, TaskStatus.AWAITING_SELECTION_OF_TRADESMAN).size() > 0 ||
                taskService.findTasksByCustomerAndStatus_NoLogic_DirectlyCallingRepo(customerId, TaskStatus.AWAITING_OFFER_BY_TRADESMEN).size() > 0 ||
                taskService.findTasksByCustomerAndStatus_NoLogic_DirectlyCallingRepo(customerId, TaskStatus.DONE).size() > 0 ||
                taskService.findTasksByCustomerAndStatus_NoLogic_DirectlyCallingRepo(customerId, TaskStatus.STARTED).size() > 0 ||
                taskService.findTasksByCustomerAndStatus_NoLogic_DirectlyCallingRepo(customerId, TaskStatus.AWAITING_TRADESMAN_ARRIVAL).size() > 0
        )
            throw new IllegalCallerException("The customer has active task requests and cannot be deleted at this point!");
        repository.deleteById(customerId);
    }

    @Override
    public Customer findById_ForDevelopmentOnly(Long customerId) {
        return repository.findCustomerById(customerId).orElseThrow(() -> new InvalidInputException("customer not found!"));
    }

    @Override
    public boolean customerExistsByEmail(String email) {
        return repository.customerExistsByEmail(email);
    }

    @Override
    public ResponseDTO findCustomerByEmail(String email) {
        Optional<Customer> customerOptional = repository.findCustomerByEmail(email);
        if (customerOptional.isEmpty())
            throw new NoSuchElementException("No such user of that type was found!");
        return mapToDTO(customerOptional.get());
    }

    @Override
    public Double getPurchasedCredit(Long cId) {
        if (!repository.existsById(cId))
            throw new InvalidInputException("Customer not found!");
        return repository.getPurchasedCredit(cId);
    }

    public FoundCustomerDTO mapToDTO(Customer customer) {
        FoundCustomerDTO foundCustomerDTO = new FoundCustomerDTO();
        foundCustomerDTO.setId(customer.getId());
        foundCustomerDTO.setActive(customer.isActive());
        foundCustomerDTO.setEmail(customer.getEmail());
        foundCustomerDTO.setRole(customer.getRole());
        foundCustomerDTO.setFirstName(customer.getFirstName());
        foundCustomerDTO.setLastName(customer.getLastName());
        foundCustomerDTO.setRegistrationDateTime(customer.getRegistrationDateTime());
        foundCustomerDTO.setPurchasedBalance(customer.getPurchasedBalance());
        foundCustomerDTO.setEmailVerified(customer.isEmailVerified());
        foundCustomerDTO.setNumberOfDoneTasks(customer.getNumberOfDoneTasks());
        foundCustomerDTO.setNumberOfRequestedTasks(customer.getNumberOfRequestedTasks());
        return foundCustomerDTO;
    }

}
