package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.Admin;
import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.repository.AdminRepository;
import ir.maktabsharif.repository.AdvancedUserSearchDAO;
import ir.maktabsharif.service.AdminService;
import ir.maktabsharif.service.base.BaseUserServiceImpl;
import ir.maktabsharif.service.dto.request.AdminRegistrationDTO;
import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;
import ir.maktabsharif.service.dto.response.FoundAdminDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import ir.maktabsharif.util.ApplicationContext;
import ir.maktabsharif.util.SemaphoreUtil;
import ir.maktabsharif.util.Validation;
import ir.maktabsharif.util.exception.InvalidInputException;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;


import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class AdminServiceImpl extends
        BaseUserServiceImpl<Admin, AdminRepository> implements
        AdminService {
    private final AdvancedUserSearchDAO advancedUserSearchDAO;

    public AdminServiceImpl(AdminRepository repository, AdvancedUserSearchDAO advancedUserSearchDAO) {
        super(repository);
        this.advancedUserSearchDAO = advancedUserSearchDAO;
    }

    @Transactional
    @Override
    public void register(AdminRegistrationDTO adminRegDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        SemaphoreUtil.acquireNewUserSemaphore();
        try {
            String email = adminRegDTO.getEmail();
            String firstName = adminRegDTO.getFirstName();
            String lastName = adminRegDTO.getLastName();
            String notHashedPassword = adminRegDTO.getNotHashedPassword();

            if (!Validation.isEmailValid(email))
                throw new InvalidInputException("Email pattern is not valid!");

            if (existsByEmail(email))
                throw new InvalidInputException("Email already exists on database!");

            if (!Validation.isPasswordValid(notHashedPassword))
                throw new InvalidInputException("Password is not Strong enough!");

            //hash password:
            String hashedPassword = getHashedPassword(notHashedPassword);
            Admin admin = new Admin();
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setEmail(email);
            admin.setPassword(hashedPassword);
            admin.setRole(UserRole.ADMIN);
            admin.setRegistrationDateTime(LocalDateTime.now());
            Set<ConstraintViolation<Admin>> violations = ApplicationContext.getValidator().validate(admin);
            if (!violations.isEmpty())
                throw new ConstraintViolationException(violations);
            repository.save(admin);
        } finally {
            SemaphoreUtil.releaseNewUserSemaphore();
        }
    }

    @Override
    public List<BaseUser> advancedUserSearch(AdvancedUserSearchDTO advancedUserSearchDTO) {
        return advancedUserSearchDAO.findUsers(advancedUserSearchDTO);
    }


    @Override
    public ResponseDTO mapToDTO(Admin admin) {
        FoundAdminDTO foundAdminDTO = new FoundAdminDTO();
        foundAdminDTO.setActive(admin.isActive());
        foundAdminDTO.setRole(admin.getRole());
        foundAdminDTO.setEmail(admin.getEmail());
        foundAdminDTO.setFirstName(admin.getFirstName());
        foundAdminDTO.setLastName(admin.getLastName());
        foundAdminDTO.setRegistrationDateTime(admin.getRegistrationDateTime());
        return foundAdminDTO;
    }
}
