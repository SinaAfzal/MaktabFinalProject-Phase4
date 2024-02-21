package ir.maktabsharif.repository;


import ir.maktabsharif.model.Admin;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
@Repository
public interface AdminRepository extends UserRepository {
    @Query("select count(a)>0 from Admin a where a.email=:email")
    boolean adminExistsByEmail(@Param("email") String email);
    @Query("select a from Admin a where a.email=:email")
    Optional<Admin> findAdminByEmail(@Param("email") String email);

}
