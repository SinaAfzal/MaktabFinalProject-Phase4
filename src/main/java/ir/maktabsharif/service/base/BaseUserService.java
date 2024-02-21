//package ir.maktabsharif.service.base;
//
//
//
//import ir.maktabsharif.model.BaseUser;
//import ir.maktabsharif.model.EmailConfirmationToken;
//import ir.maktabsharif.service.dto.request.LoginDTO;
//import ir.maktabsharif.service.dto.request.UserChangePasswordDTO;
//import ir.maktabsharif.service.dto.request.UserEditProfileDTO;
//import ir.maktabsharif.service.dto.response.ResponseDTO;
//
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//
//public interface BaseUserService<T extends BaseUser>{
//
//    boolean existsByEmail(String email);
//    /**this mehtod was edited heavily during phase3 to remove security context*/
//    void editProfile(Long id,UserEditProfileDTO userEditProfileDTO) throws InterruptedException;
//    ResponseDTO findByEmail(String email);
//
//    /**this mehtod was edited heavily during phase3 to remove security context*/
//    void changePassword(String email,UserChangePasswordDTO userChPassDTO) throws NoSuchAlgorithmException, InvalidKeySpecException;//uses loggedIn user's data
//
//
//    ResponseDTO mapToDTO(T t);
//
//}
