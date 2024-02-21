package ir.maktabsharif.service;


import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.service.dto.request.AdminRegistrationDTO;
import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;
import ir.maktabsharif.service.dto.response.FoundAdminDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import jakarta.mail.MessagingException;

import java.util.List;

public interface AdminService extends UserService {

    boolean adminExistsByEmail(String email);

    ResponseDTO findAdminByEmail(String email);

    void register(AdminRegistrationDTO adminRegistrationDTO) throws MessagingException, InterruptedException;

    List<BaseUser> advancedUserSearch(AdvancedUserSearchDTO advancedUserSearchDTO);
}
