//package ir.maktabsharif.repository;
//
//import ir.maktabsharif.model.Admin;
//import ir.maktabsharif.model.BaseUser;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface BaseUserRepository<T extends BaseUser> extends JpaRepository<T,Long> {
//
//    Optional<T> findById(Long id);
//    boolean existsByEmail(String email);
//    Optional<T> findByEmail(String email);
//    Optional<T> findByEmailAndPassword(String email, String hashedPassword);
//
//}
