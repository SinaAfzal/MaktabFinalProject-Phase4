package ir.maktabsharif.repository;



import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.model.enumeration.TradesManStatus;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


public interface TradesManRepository extends BaseUserRepository<TradesMan> {
    boolean existsById(Long tradesManId);
    boolean existsByEmail(String email);
    Optional<TradesMan> findByEmailAndPassword(String email, String hashedPassword);

    @Query("select count(t)>0 from TradesMan t where t.status='APPROVED' and t.id=:tradesManId")
    boolean isTradesManApproved(@Param("tradesManId") Long tradesManId);

   // void downloadTradesManAvatar(Long tradesManId, String savePath) throws IOException;

    List<TradesMan> findTradesManByStatus(TradesManStatus status);
    @Query("select t from Task t where t.tradesManWhoGotTheJob.id=:tId and t.status=:status")
    List<Task> findTasksByWinnerTradesManAndStatus(@Param("tId") Long tradeManId, @Param("status") TaskStatus status);

}
