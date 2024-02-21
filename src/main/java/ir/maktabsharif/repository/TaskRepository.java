package ir.maktabsharif.repository;


import ir.maktabsharif.model.*;
import ir.maktabsharif.model.enumeration.TaskStatus;
import org.hibernate.sql.Update;
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
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findById(Long categoryId);

    boolean existsById(Long taskId);

    List<Task> findTasksByStatus(TaskStatus status);

    @Query(value = "SELECT * FROM tasks t JOIN category_tradesmen ct ON t.sub_category_id=ct.category_id WHERE t.status=:status AND ct.trades_men_id=:tId", nativeQuery = true)
    List<Task> findTasksByStatusAndTradesManCategories(@Param("status") String status, @Param("tId") Long tradesManId);//note if you pass enum in native query it will return "jdbc mapping is null" exception. you should pass string instead of enum

    List<Task> findTasksByCustomerIdAndStatus(Long customerId, TaskStatus status);

    @Query("select t from Task t where t.tradesManWhoGotTheJob.id=:tId and t.status=:status")
    List<Task> findTasksByWinnerTradesManAndStatus(@Param("tId") Long tradeManId, @Param("status") TaskStatus status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tasks t SET selected_proposal_id=NULL WHERE id=:tId ;" +
            "DELETE FROM proposal p WHERE task_id=:tId ;" +
            "DELETE FROM tasks t WHERE id=:tId "
            , nativeQuery = true)
    void deleteById(@Param("tId") Long taskId);

    @Query("select c from Category c where c.id=:cId")
    Optional<Category> findCategoryByCategoryId(@Param("cId") Long categoryId);

    @Query("select p from Proposal p where p.taskId=:taskId")
    List<Proposal> findProposalsByTaskId(@Param("taskId") Long taskId);

    @Query(value = "SELECT p FROM Proposal p JOIN Task t ON (t.id = p.taskId AND t.tradesManWhoGotTheJob.id=p.tradesManId) WHERE t.id=:taskId")
    Optional<Proposal> findWinnerProposal(@Param("taskId") Long taskId);

    @Query("select p from Proposal p where p.id=:pId")
    Optional<Proposal> findProposalByProposalId(@Param("pId") Long proposalId);

    @Modifying
    @Query("update TradesMan t set t.rating=:rating where t.id=:tId")
    void updateTradesManRating(@Param("tId") Long tradesmanId, @Param("rating") Float rating);

    @Query("select count(t)>0 from TradesMan t where t.id=:tId")
    boolean tradesmanExistsByTradesManId(@Param("tId") Long tradesmanId);

    @Query("select t from TradesMan t where t.id=:tId")
    Optional<TradesMan> findTradesManByTradesManId(@Param("tId") Long tradesmanId);

    @Query("select count(c)>0 from Customer c where c.id=:cId")
    boolean customerExistsByCustomerId(@Param("cId") Long customerId);

    @Query("select c from Customer c where c.id=:cId")
    Optional<Customer> findCustomerById(@Param("cId") Long cId);

    @Modifying
    @Query("update Customer c set c.purchasedBalance=:balance where c.id=:cId")
    void updateCustomerBalance(@Param("cId") Long customerId, @Param("balance") Double newBalance);

    @Modifying
    @Query("update Customer c set c.numberOfDoneTasks=:numDoneTasks where c.id=:cId")
    void updateCustomerNumberOfDoneTasks(@Param("cId") Long customerId, @Param("numDoneTasks") Long numberOfDoneTasks);

    @Modifying
    @Query("update Customer c set c.numberOfRequestedTasks=:numReqTasks where c.id=:cId")
    void updateCustomerNumberOfRequestedTasks(@Param("cId") Long customerId, @Param("numReqTasks") Long numberOfRequestedTasks);
}
