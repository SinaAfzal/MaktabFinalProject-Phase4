package ir.maktabsharif.service;



import ir.maktabsharif.model.Admin;
import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.service.base.BaseUserService;
import ir.maktabsharif.service.dto.request.AdminRegistrationDTO;
import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface AdminService extends BaseUserService<Admin> {

void register(AdminRegistrationDTO adminRegistrationDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException;

List<BaseUser> advancedUserSearch(AdvancedUserSearchDTO advancedUserSearchDTO);
}
