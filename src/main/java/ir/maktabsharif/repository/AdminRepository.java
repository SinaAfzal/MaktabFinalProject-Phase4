package ir.maktabsharif.repository;


import ir.maktabsharif.model.Admin;


import java.util.Optional;

public interface AdminRepository extends BaseUserRepository<Admin> {
    boolean existsByEmail(String email);
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByEmailAndPassword(String email, String hashedPassword);
}
