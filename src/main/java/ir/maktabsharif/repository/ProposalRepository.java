package ir.maktabsharif.repository;


import ir.maktabsharif.model.Category;
import ir.maktabsharif.model.Proposal;
import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.TradesMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Optional<Proposal> findById(Long categoryId);
    boolean existsById(Long proposalId);

    List<Proposal> findByTradesManId(Long tradesManId);

    List<Proposal> findByTaskId(Long taskId);

    @Query("select t from Task t where t.id=:tId")
    Optional<Task> findTaskByTaskId(@Param("tId") Long taskId);
    @Query("select count(t)>0 from TradesMan t where t.id=:tId")
    boolean tradesmanExistsByTradesmanId(@Param("tId") Long tradesmanId);
    @Query("select t from TradesMan t where t.id=:tId")
    Optional<TradesMan> findTradesmanByTradesManId(@Param("tId") Long tradesmanId);
    @Query("select c from Category c where c.id=:cId")
    Optional<Category> findCategoryByCategoryId(@Param("cId") Long categoryId);


}
