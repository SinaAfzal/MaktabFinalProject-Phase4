package ir.maktabsharif.repository;


import com.fasterxml.jackson.databind.annotation.JsonAppend;
import ir.maktabsharif.model.Category;
import ir.maktabsharif.model.Proposal;
import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.TradesMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Optional<Proposal> findById(Long categoryId);

    boolean existsById(Long proposalId);

    List<Proposal> findByTradesManId(Long tradesManId);

    List<Proposal> findByTaskId(Long taskId);
    @Query(value = "select p from Proposal p join Task t on p.taskId=t.id join TradesMan tr on p.tradesManId=tr.id where p.taskId=:taskId order by tr.rating asc ")
    List<Proposal> findProposalsByTaskIdSortByTradesManScoreAscending(@Param("taskId") Long taskId);

    @Query(value = "select p from Proposal p join Task t on p.taskId=t.id where p.taskId=:taskId order by p.proposedPrice asc ")
    List<Proposal> findProposalsByTaskIdSortByProposedPriceAscending(@Param("taskId") Long taskId);

    @Query("select t from Task t where t.id=:tId")
    Optional<Task> findTaskByTaskId(@Param("tId") Long taskId);

    @Query("select count(t)>0 from TradesMan t where t.id=:tId")
    boolean tradesmanExistsByTradesmanId(@Param("tId") Long tradesmanId);

    @Query("select t from TradesMan t where t.id=:tId")
    Optional<TradesMan> findTradesmanByTradesManId(@Param("tId") Long tradesmanId);

    @Query("select c from Category c where c.id=:cId")
    Optional<Category> findCategoryByCategoryId(@Param("cId") Long categoryId);
    @Query("select count(p)>0 from Proposal p where p.taskId=:taskId and p.tradesManId=:tradesmanId")
    boolean didTradesmanSendProposalForTaskBefore(@Param("tradesmanId") Long tradesmanId,@Param("taskId") Long taskId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE tasks t SET selected_proposal_id=NULL WHERE selected_proposal_id=:pId ;" +
            "DELETE FROM proposal p WHERE id=:pId ", nativeQuery = true)
    void deleteById(@Param("pId") Long proposalId);
}
