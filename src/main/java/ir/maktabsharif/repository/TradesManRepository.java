package ir.maktabsharif.repository;


import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.model.enumeration.TradesManStatus;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
public interface TradesManRepository extends UserRepository {


    @Query("select count(t)>0 from TradesMan t where t.status='APPROVED' and t.id=:tradesManId")
    boolean isTradesManApproved(@Param("tradesManId") Long tradesManId);

    @Query("select t from TradesMan t where t.status=?1")
    List<TradesMan> findTradesManByStatus(TradesManStatus status);

    @Query("select t from Task t where t.tradesManWhoGotTheJob.id=:tId and t.status=:status")
    List<Task> findTasksByWinnerTradesManAndStatus(@Param("tId") Long tradeManId, @Param("status") TaskStatus status);

    @Query("select t from TradesMan t where t.id=:tId")
    Optional<TradesMan> findTradesManById(@Param("tId") Long id);

    @Query("select t from TradesMan t where t.email=:email")
    Optional<TradesMan> findTradesManByEmail(@Param("email") String email);

    @Query("select t.earnedCredit from TradesMan t where t.id=:tId")
    Double getEarnedCreditBalance(@Param("tId") Long tradesmanId);

    @Transactional
    @Query(value = "DELETE FROM category_tradesmen ct WHERE trades_men_id=:tId ; " +
            "DELETE FROM email_confirmation_token e WHERE base_user_id=:tId ; " +
            "UPDATE proposal p SET trades_man_id=NULL WHERE trades_man_id=:tId ; " +
            "UPDATE tasks t SET trades_man_who_got_the_job_id=NULL WHERE trades_man_who_got_the_job_id=:tId ; " +
            "DELETE FROM users u WHERE id=:tId "
            , nativeQuery = true)
    @Modifying
    void deleteById(@Param("tId") Long tradesmanId);

}
