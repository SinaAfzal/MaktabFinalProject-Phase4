package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.Admin;
import ir.maktabsharif.model.Customer;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.repository.CustomerRepository;
import ir.maktabsharif.repository.TaskRepository;
import ir.maktabsharif.service.CustomerService;
import ir.maktabsharif.service.TaskService;
import ir.maktabsharif.service.base.BaseUserServiceImpl;
import ir.maktabsharif.service.dto.request.CustomerRegistrationDTO;
import ir.maktabsharif.service.dto.response.FoundCustomerDTO;
import ir.maktabsharif.util.*;
import ir.maktabsharif.util.exception.AccessDeniedException;
import ir.maktabsharif.util.exception.ExistingEntityCannotBeFetchedException;
import ir.maktabsharif.util.exception.InvalidInputException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Set;
@Service
public class CustomerServiceImpl extends
        BaseUserServiceImpl<Customer, CustomerRepository> implements
        CustomerService {
    private final TaskService taskService;

    public CustomerServiceImpl(CustomerRepository repository, TaskService taskService) {
        super(repository);
        this.taskService = taskService;
    }

    @Override
    public void register(CustomerRegistrationDTO cuReDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
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
            String hashedPassword = getHashedPassword(notHashedPassword);

            Customer customer = new Customer();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setPassword(hashedPassword);
            customer.setRole(UserRole.CUSTOMER);
            customer.setRegistrationDateTime(LocalDateTime.now());
            customer.setPurchasedBalance(0.00D);
            Set<ConstraintViolation<Customer>> violations = ApplicationContext.getValidator().validate(customer);
            if (!violations.isEmpty())
                throw new ConstraintViolationException(violations);
            repository.save(customer);
        } finally {
            SemaphoreUtil.releaseNewUserSemaphore();
        }
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        if (!repository.existsById(customerId))
            throw new IllegalArgumentException("Invalid customer ID!");
        Customer customer = repository.findById(customerId).orElseThrow(() -> new ExistingEntityCannotBeFetchedException("Customer exists but cannot be fetched!"));
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
        return repository.findById(customerId).orElseThrow(()->new InvalidInputException("customer not found!"));
    }

    @Override
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
        return foundCustomerDTO;
    }

}
