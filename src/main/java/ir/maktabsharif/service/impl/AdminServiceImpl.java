package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.Admin;
import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.repository.AdminRepository;
import ir.maktabsharif.repository.AdvancedUserSearchDAO;
import ir.maktabsharif.repository.UserRepository;
import ir.maktabsharif.service.*;
import ir.maktabsharif.service.dto.request.AdminRegistrationDTO;
import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;
import ir.maktabsharif.service.dto.response.FoundAdminDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import ir.maktabsharif.util.SemaphoreUtil;
import ir.maktabsharif.util.exception.InvalidInputException;

import jakarta.mail.MessagingException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl extends
        UserServiceImpl implements
        AdminService {
    private final AdvancedUserSearchDAO advancedUserSearchDAO;
    private final AdminRepository repository;

    public AdminServiceImpl(UserRepository userRepository, EmailConfirmationTokenService emailConfirmationTokenService, EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder, AdvancedUserSearchDAO advancedUserSearchDAO, AdminRepository repository) {
        super(userRepository, emailConfirmationTokenService, emailService, bCryptPasswordEncoder);
        this.advancedUserSearchDAO = advancedUserSearchDAO;
        this.repository = repository;
    }


    @Override
    public boolean adminExistsByEmail(String email) {
        return repository.adminExistsByEmail(email);
    }

    @Override
    public ResponseDTO findAdminByEmail(String email) {
        Optional<Admin> adminOptional = repository.findAdminByEmail(email);
        if (adminOptional.isEmpty())
            throw new NoSuchElementException("No such user of that type was found!");
        return mapToDTO(adminOptional.get());
    }

    @Transactional
    @Override
    public void register(AdminRegistrationDTO adminRegDTO) throws InterruptedException, MessagingException {
        SemaphoreUtil.acquireNewUserSemaphore();
        try {
            if (existsByEmail(adminRegDTO.getEmail()))
                throw new InvalidInputException("Email already exists on database!");
            Admin admin = mapToEntity(adminRegDTO);
            repository.save(admin);
            sendEmailVerificationEmail(adminRegDTO.getEmail());
        } finally {
            SemaphoreUtil.releaseNewUserSemaphore();
        }
    }

    @Override
    public List<BaseUser> advancedUserSearch(AdvancedUserSearchDTO advancedUserSearchDTO) {
        return advancedUserSearchDAO.findUsers(advancedUserSearchDTO);
    }

    @Transactional
    public ResponseDTO mapToDTO(Admin admin) {
        FoundAdminDTO foundAdminDTO = new FoundAdminDTO();
        foundAdminDTO.setActive(admin.isActive());
        foundAdminDTO.setRole(admin.getRole());
        foundAdminDTO.setEmail(admin.getEmail());
        foundAdminDTO.setFirstName(admin.getFirstName());
        foundAdminDTO.setLastName(admin.getLastName());
        foundAdminDTO.setRegistrationDateTime(admin.getRegistrationDateTime());
        foundAdminDTO.setEmailVerified(admin.isEmailVerified());
        return foundAdminDTO;
    }

    public Admin mapToEntity(AdminRegistrationDTO dto){
        return Admin.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .role(UserRole.ROLE_ADMIN)
                .registrationDateTime(LocalDateTime.now())
                .isActive(true)
                .password(bCryptPasswordEncoder.encode(dto.getNotHashedPassword()))
                .build();
    }

}
