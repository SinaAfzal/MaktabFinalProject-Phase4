package ir.maktabsharif.service;

import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.service.dto.request.UserChangePasswordDTO;
import ir.maktabsharif.service.dto.request.UserEditProfileDTO;
import jakarta.mail.MessagingException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public interface UserService {
    Optional<BaseUser> findByUserName(String username);
    int verifyEmailByTokenAndEnableUser(String tokenSerial);
    void sendEmailVerificationEmail(String userEmail) throws MessagingException;

    Optional<BaseUser> findByEmailAndRole(String email,UserRole role);
    boolean existsByEmail(String email);
    boolean existsByEmailAndRole(String email, UserRole role);

    Optional<BaseUser> findByIdAndRole(Long id, UserRole role);

    void editProfile(Long id, UserEditProfileDTO userEditProfileDTO) throws InterruptedException;

    void changePassword(String email, UserChangePasswordDTO userChPassDTO) throws NoSuchAlgorithmException, InvalidKeySpecException;//uses loggedIn user's data

}
