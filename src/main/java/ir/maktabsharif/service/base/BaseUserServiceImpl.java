package ir.maktabsharif.service.base;

import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.repository.BaseUserRepository;
import ir.maktabsharif.service.dto.request.LoginDTO;
import ir.maktabsharif.service.dto.request.UserChangePasswordDTO;
import ir.maktabsharif.service.dto.request.UserEditProfileDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import ir.maktabsharif.util.*;
import ir.maktabsharif.util.exception.AccessDeniedException;
import ir.maktabsharif.util.exception.InvalidInputException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public abstract class BaseUserServiceImpl<
        T extends BaseUser,
        R extends BaseUserRepository<T>
        > implements
        BaseUserService<T> {

    protected final R repository;

    protected BaseUserServiceImpl(R repository) {
        this.repository = repository;
    }


    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public ResponseDTO findByEmail(String email) {
        Optional<T> tOptional = repository.findByEmail(email);
        if (tOptional.isEmpty())
            throw new NoSuchElementException("No such user of that type was found!");
        return mapToDTO(tOptional.get());
    }

    //todo check if notApproved tradesman or deactivated user is trying to login. if so then block the request.
    @Override
    public void login(LoginDTO loginDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String email = loginDTO.getEmail();
        String notHashedPassword = loginDTO.getNotHashedPassword();

        String hashedPassword = getHashedPassword(notHashedPassword);

        Optional<T> loggedInOptional = repository.findByEmailAndPassword(email, hashedPassword);
        if (loggedInOptional.isEmpty())
            throw new InvalidInputException("Invalid credentials!");
        if (!(loggedInOptional.get()).isActive())
            throw new AccessDeniedException("Sorry! Your account is not active!");
        SecurityContext.fillContext(loggedInOptional.get());
    }

    /**
     * this method was edited heavily during phase3 to remove security context
     */
    @Transactional
    @Override
    public void changePassword(String email, UserChangePasswordDTO userChPassDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        BaseUser loggedInUser = SecurityContext.getCurrentUser();
//        if (loggedInUser == null)
//            throw new AccessDeniedException("you are not logged in!");
        //check to see if the old password matches the one in the database. we can use the login() method:
        LoginDTO toCheckOldPassword = new LoginDTO(email, userChPassDTO.getNotHashedOldPassword());
        login(toCheckOldPassword);

        BaseUser loggedInUser = repository.findByEmail(email).orElseThrow(() -> new InvalidInputException("user not found"));
        //validate new password
        boolean passwordValid = Validation.isPasswordValid(userChPassDTO.getNotHashedNewPassword());
        if (passwordValid) {
            String hashedPassword = getHashedPassword(userChPassDTO.getNotHashedNewPassword());
            loggedInUser.setPassword(hashedPassword);
            Set<ConstraintViolation<BaseUser>> violations = ApplicationContext.getValidator().validate(loggedInUser);
            if (!violations.isEmpty())
                throw new ConstraintViolationException(violations);
            repository.save((T) loggedInUser);
        } else
            throw new InvalidInputException("The pattern of new password is not valid!");
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    @Transactional
    @Override
    public void editProfile(Long id, UserEditProfileDTO userEditProfileDTO) throws InterruptedException {
        SemaphoreUtil.acquireNewUserSemaphore();
        try {
//            BaseUser currentUser = SecurityContext.getCurrentUser();
//            if (SecurityContext.getCurrentUser() == null)
//                throw new AccessDeniedException("Please login first!");
            BaseUser currentUser = repository.findById(id).orElseThrow(() -> new InvalidInputException("user not found!"));
            String firstName = userEditProfileDTO.getFirstName();
            String lastName = userEditProfileDTO.getLastName();
            String email = userEditProfileDTO.getEmail();

            //validate new email
            if (!Validation.isEmailValid(email))
                throw new InvalidInputException("Email pattern is not valid!");
            //check to see if the new email already exists
            if (existsByEmail(email) && !repository.findByEmail(email).get().getId().equals(currentUser.getId()))
                throw new InvalidInputException("This email already exists on database!");

            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            Set<ConstraintViolation<BaseUser>> violations = ApplicationContext.getValidator().validate(currentUser);
            if (!violations.isEmpty())
                throw new ConstraintViolationException(violations);
            repository.save((T) currentUser);
//        SecurityContext.fillContext(currentUser);
        } finally {
            SemaphoreUtil.releaseNewUserSemaphore();
        }

    }

    protected static String getHashedPassword(String notHashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //password is hashed using PBKDF2 and the concept of salt
        // salt is a random sequence that is generated for each new hash.
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
        //for simplicity here I used static salt without storing it in database. this way a certain string would be hashed to the same byte[] in different hashing attempts.
        byte[] salt = "myStaticSaltForSimplicity".getBytes(StandardCharsets.UTF_8);//todo ForFuture salt should be a randomly generated key and be stored in database. for login the notHashed password should be hashed using the stored salt and be compared with the hashed password in database.
        KeySpec spec = new PBEKeySpec(notHashedPassword.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        String hashedPassword = Arrays.toString(factory.generateSecret(spec).getEncoded());
        return hashedPassword;
    }

}
