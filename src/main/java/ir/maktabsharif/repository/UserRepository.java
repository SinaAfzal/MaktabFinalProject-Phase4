package ir.maktabsharif.repository;

import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BaseUser, Long> {
    Optional<BaseUser> findBaseUserByEmail(String email);
    Optional<BaseUser> findBaseUserByEmailAndRole(String email, UserRole role);
    boolean existsBaseUserByEmail(String email);
    boolean existsBaseUserByEmailAndRole(String email, UserRole role);

    Optional<BaseUser> findBaseUserByIdAndRole(Long id, UserRole role);



    @Modifying
    @Query("update BaseUser u set u.isEmailVerified=:state where u.email=:email")
    int toggleIsEmailVerified(@Param("email") String email,@Param("state") boolean state);
    @Modifying
    @Query("update TradesMan t set t.status=:status where t.id=:tId")
    int setTradesManStatus(@Param("tId") Long tradesManId, @Param("status")TradesManStatus status);

}
