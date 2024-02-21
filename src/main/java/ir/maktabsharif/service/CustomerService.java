package ir.maktabsharif.service;


import ir.maktabsharif.model.Customer;
import ir.maktabsharif.service.dto.request.CustomerRegistrationDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import jakarta.mail.MessagingException;


public interface CustomerService extends UserService {

    void register(CustomerRegistrationDTO cuReDTO) throws InterruptedException, MessagingException;

    void deleteCustomerById(Long customerId);

    Customer findById_ForDevelopmentOnly(Long customerId);

    boolean customerExistsByEmail(String email);
    ResponseDTO findCustomerByEmail(String email);
    Double getPurchasedCredit(Long cId);
}
