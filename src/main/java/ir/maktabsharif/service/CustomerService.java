package ir.maktabsharif.service;


import ir.maktabsharif.model.Customer;
import ir.maktabsharif.service.base.BaseUserService;
import ir.maktabsharif.service.dto.request.CustomerRegistrationDTO;


import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface CustomerService extends BaseUserService<Customer> {

    void register(CustomerRegistrationDTO cuReDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException;

    void deleteCustomerById(Long customerId);

    Customer findById_ForDevelopmentOnly(Long customerId);
}
