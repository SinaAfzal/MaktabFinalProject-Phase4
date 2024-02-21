package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.model.Customer;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.repository.AdvancedUserSearchDAO;
import ir.maktabsharif.service.AdvancedUserSearchService;
import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;
import ir.maktabsharif.service.dto.response.FoundUserDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdvancedUserSearchServiceImpl implements AdvancedUserSearchService {
    private final AdvancedUserSearchDAO advancedUserSearchDAO;

    public AdvancedUserSearchServiceImpl(AdvancedUserSearchDAO advancedUserSearchDAO) {
        this.advancedUserSearchDAO = advancedUserSearchDAO;
    }

    @Override
    public List<ResponseDTO> findUsers(AdvancedUserSearchDTO advancedUserSearchDTO) {
        List<BaseUser> users = advancedUserSearchDAO.findUsers(advancedUserSearchDTO);
        return users.stream().map(this::mapToDTO).toList();
    }

    public ResponseDTO mapToDTO(BaseUser user) {
        FoundUserDTO foundUserDTO = new FoundUserDTO();
        foundUserDTO.setActive(user.isActive());
        foundUserDTO.setId(user.getId());
        foundUserDTO.setEmail(user.getEmail());
        foundUserDTO.setLastName(user.getLastName());
        foundUserDTO.setRegistrationDateTime(user.getRegistrationDateTime());
        foundUserDTO.setFirstName(user.getFirstName());
        foundUserDTO.setRole(user.getRole());
        foundUserDTO.setEmailVerified(user.isEmailVerified());

        if (user instanceof Customer) {
            foundUserDTO.setPurchasedBalance(((Customer) user).getPurchasedBalance());
            foundUserDTO.setNumberOfRequestedTasks(((Customer) user).getNumberOfRequestedTasks());
            foundUserDTO.setNumberOfDoneTasks(((Customer) user).getNumberOfDoneTasks());
        }

        if (user instanceof TradesMan) {
            foundUserDTO.setAvatar(((TradesMan) user).getAvatar());
            foundUserDTO.setEarnedCredit(((TradesMan) user).getEarnedCredit());
            foundUserDTO.setRating(((TradesMan) user).getRating());
            foundUserDTO.setStatus(((TradesMan) user).getStatus());
            foundUserDTO.setNumberOfProposalsSent(((TradesMan) user).getNumberOfProposalsSent());
            foundUserDTO.setNumberOfDoneTasks(((TradesMan) user).getNumberOfDoneTasks());
        }

        return foundUserDTO;
    }
}
