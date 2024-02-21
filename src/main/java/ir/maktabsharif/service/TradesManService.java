package ir.maktabsharif.service;


import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.service.dto.request.TradesManRegistrationDTO;
import ir.maktabsharif.service.dto.response.FoundTradesManDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface TradesManService extends UserService {
    boolean isTradesManApproved(Long tradesManId);

    void downloadTradesManAvatar(Long tradesManId, String savePath) throws IOException;

    TradesMan findById_ForDevelopmentOnly(Long tradesmanId);

    void register(TradesManRegistrationDTO trdRegDTO) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, MessagingException;

    TradesMan saveJustForDeveloperUse(TradesMan tradesMan);

    void deleteTradesManById(Long tradesManId);

    void changeTradesManStatus(Long tradesManId, TradesManStatus status);

    List<FoundTradesManDTO> findTradesMenByStatus(TradesManStatus status);

    void updateTradesManCreditByProposedPrice(TradesMan tradesMan, Double proposedPrice);

    boolean tradesManExistsByEmail(String email);

    ResponseDTO findTradesManByEmail(String email);

    Double getEarnedCredit(Long tradesmanId);

}
