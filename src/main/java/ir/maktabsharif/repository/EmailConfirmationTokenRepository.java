package ir.maktabsharif.repository;

import ir.maktabsharif.model.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {
    Optional<EmailConfirmationToken> findByTokenSerial(String token);

    @Modifying
    @Query("update EmailConfirmationToken ect set ect.confirmedAt=?2 where ect.tokenSerial=?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
