package ir.maktabsharif.repository;


import ir.maktabsharif.model.Admin;
import ir.maktabsharif.model.Customer;
import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.TradesMan;

import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.service.CustomerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends UserRepository {

    @Query("select count(c)>0 from Customer c where c.id=:cId")
    boolean customerExistsById(@Param("cId") Long customerId);

    @Query("select count(c)>0 from Customer c where c.email=:email")
    boolean customerExistsByEmail(@Param("email") String email);

    @Query("select c from Customer c where c.id=:cId")
    Optional<Customer> findCustomerById(@Param("cId") Long customerId);

    @Query("select c from Customer c where c.email=:email")
    Optional<Customer> findCustomerByEmail(@Param("email") String email);

    @Query("select c.purchasedBalance from Customer c where c.id=:cId")
    Double getPurchasedCredit(@Param("cId") Long customerId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tasks t WHERE customer_id=:cId ;" +
            "DELETE FROM email_confirmation_token e WHERE base_user_id=:cId ;" +
            "UPDATE tasks t SET customer_id= NULL WHERE customer_id=:cId ;" +
            "DELETE FROM users u WHERE id=:cId;", nativeQuery = true)
    void deleteById(@Param("cId") Long customerId);
}
