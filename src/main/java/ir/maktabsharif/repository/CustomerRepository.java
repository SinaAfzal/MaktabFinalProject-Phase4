package ir.maktabsharif.repository;


import ir.maktabsharif.model.Admin;
import ir.maktabsharif.model.Customer;
import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.TradesMan;

import ir.maktabsharif.model.enumeration.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseUserRepository<Customer> {
    boolean existsById(Long customerId);

    boolean existsByEmail(String email);

    Optional<Customer> findByEmailAndPassword(String email, String hashedPassword);
}
