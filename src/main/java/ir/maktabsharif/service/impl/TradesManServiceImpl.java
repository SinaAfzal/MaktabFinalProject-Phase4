package ir.maktabsharif.service.impl;


import ir.maktabsharif.model.Admin;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.repository.TradesManRepository;
import ir.maktabsharif.service.TradesManService;
import ir.maktabsharif.service.base.BaseUserServiceImpl;
import ir.maktabsharif.service.dto.request.TradesManRegistrationDTO;
import ir.maktabsharif.service.dto.response.FoundTradesManDTO;
import ir.maktabsharif.util.*;
import ir.maktabsharif.util.exception.AccessDeniedException;
import ir.maktabsharif.util.exception.ExistingEntityCannotBeFetchedException;
import ir.maktabsharif.util.exception.InvalidInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TradesManServiceImpl extends
        BaseUserServiceImpl<TradesMan, TradesManRepository>
        implements TradesManService {

    public TradesManServiceImpl(TradesManRepository repository) {
        super(repository);
    }

    @Override
    public boolean isTradesManApproved(Long tradesManId) {
        if (!repository.existsById(tradesManId))
            throw new InvalidInputException("TradesMan not found!");
        return repository.isTradesManApproved(tradesManId);
    }

    public TradesMan findById_ForDevelopmentOnly(Long tradesmanId) {
        return repository.findById(tradesmanId).orElseThrow(()->new InvalidInputException("Tradesman not found!"));
    }

    @Override
    public void downloadTradesManAvatar(Long tradesManId, String savePath) throws IOException {
        if (!repository.existsById(tradesManId))
            throw new InvalidInputException("TradesMan not found!");
        byte[] avatar = repository.findById(tradesManId).get().getAvatar();
        Files.write(Paths.get(savePath), avatar);
    }

    @Override
    public void register(TradesManRegistrationDTO trdRegDTO) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        SemaphoreUtil.acquireNewUserSemaphore();
        try {
            String imagePath = trdRegDTO.getImagePath();
            String firstName = trdRegDTO.getFirstName();
            String lastName = trdRegDTO.getLastName();
            String password = trdRegDTO.getPassword();
            String email = trdRegDTO.getEmail();
            if (!Validation.isImageValid(imagePath))
                throw new IllegalArgumentException("Image should be in '.jpg' format with size less than 300kB.");
            if (!Validation.isPasswordValid(password))
                throw new IllegalArgumentException("Password is not strong enough!");
            if (!Validation.isEmailValid(email))
                throw new IllegalArgumentException("Email format is not valid!");
            if (existsByEmail(email))
                throw new IllegalArgumentException("User already exists on database!");

            // Read image file into a byte array
            Path path = Paths.get(imagePath);
            byte[] imageData = Files.readAllBytes(path);
            TradesMan tradesMan = new TradesMan();
            tradesMan.setAvatar(imageData);
            tradesMan.setStatus(TradesManStatus.NEW);
            tradesMan.setEarnedCredit(0.00);
            tradesMan.setEmail(email);
            tradesMan.setRole(UserRole.TRADESMAN);
            tradesMan.setRegistrationDateTime(LocalDateTime.now());
            tradesMan.setFirstName(firstName);
            tradesMan.setLastName(lastName);
            String hashedPassword = getHashedPassword(password);
            tradesMan.setPassword(hashedPassword);
            tradesMan.setActive(false);
            Set<ConstraintViolation<TradesMan>> violations = ApplicationContext.getValidator().validate(tradesMan);
            if (!violations.isEmpty())
                throw new ConstraintViolationException(violations);
            repository.save(tradesMan);
        } finally {
            SemaphoreUtil.releaseNewUserSemaphore();
        }
    }

    @Override
    public TradesMan saveJustForDeveloperUse(TradesMan tradesMan) {
        return repository.save(tradesMan);
    }

    @Override
    public void deleteTradesManById(Long tradesManId) {
        Optional<TradesMan> tradesManOptional = repository.findById(tradesManId);
        if (tradesManOptional.isEmpty())
            throw new IllegalArgumentException("tradesMan could not be fetched!");
        if (repository.findTasksByWinnerTradesManAndStatus(tradesManId, TaskStatus.DONE).size() != 0 ||
                repository.findTasksByWinnerTradesManAndStatus(tradesManId, TaskStatus.STARTED).size() != 0 ||
                repository.findTasksByWinnerTradesManAndStatus(tradesManId, TaskStatus.AWAITING_TRADESMAN_ARRIVAL).size() != 0)
            throw new IllegalCallerException("The tradesman has currently active tasks and cannot be deleted at this point!");
        repository.deleteById(tradesManId);
    }

    @Override
    public void changeTradesManStatus(Long tradesManId, TradesManStatus status) {
//        if (!(SecurityContext.getCurrentUser() instanceof Admin))
//            throw new AccessDeniedException("Only an admin can access this service!");
        if (!repository.existsById(tradesManId))
            throw new IllegalArgumentException("TradesMan was not found!");
        TradesMan tradesMan = repository.findById(tradesManId).orElseThrow(() -> new ExistingEntityCannotBeFetchedException("TradesMan exists but could not be fetched!"));
        if (repository.findTasksByWinnerTradesManAndStatus(tradesManId, TaskStatus.STARTED).size() != 0 ||
                repository.findTasksByWinnerTradesManAndStatus(tradesManId, TaskStatus.DONE).size() != 0 ||
                repository.findTasksByWinnerTradesManAndStatus(tradesManId, TaskStatus.AWAITING_TRADESMAN_ARRIVAL).size() != 0)
            throw new InputMismatchException("TradesMan's status cannot be changed when he/she has active tasks!");
        if (status == TradesManStatus.APPROVED)
            tradesMan.setActive(true);
        else if (tradesMan.getStatus() == TradesManStatus.APPROVED)
            tradesMan.setActive(false);
        tradesMan.setStatus(status);
        repository.save(tradesMan);
    }

    @Override
    public List<FoundTradesManDTO> findTradesMenByStatus(TradesManStatus status) {
//        if (!(SecurityContext.getCurrentUser() instanceof Admin))
//            throw new AccessDeniedException("Only admins have access to this service!");
        List<TradesMan> tradesManList = repository.findTradesManByStatus(status);
        if (tradesManList.isEmpty())
            throw new NoSuchElementException("no tradesmen with that status was found!");
        List<FoundTradesManDTO> tradesManDTOs = new ArrayList<>();
        for (TradesMan t : tradesManList)
            tradesManDTOs.add(mapToDTO(t));
        return tradesManDTOs;
    }

    @Override
    @Transactional
    public void updateTradesManCreditByProposedPrice(TradesMan tradesMan, Double proposedPrice) {
        tradesMan.setEarnedCredit(tradesMan.getEarnedCredit() + (Policy.getShareOfTradesManFromPrice() * proposedPrice));
        repository.save(tradesMan);
    }

    @Override
    public FoundTradesManDTO mapToDTO(TradesMan tradesMan) {
        FoundTradesManDTO foundTradesManDTO = new FoundTradesManDTO();
        foundTradesManDTO.setActive(tradesMan.isActive());
        foundTradesManDTO.setId(tradesMan.getId());
        foundTradesManDTO.setAvatar(tradesMan.getAvatar());
        foundTradesManDTO.setEarnedCredit(tradesMan.getEarnedCredit());
        foundTradesManDTO.setRating(tradesMan.getRating());
        foundTradesManDTO.setEmail(tradesMan.getEmail());
        foundTradesManDTO.setLastName(tradesMan.getLastName());
        foundTradesManDTO.setRegistrationDateTime(tradesMan.getRegistrationDateTime());
        foundTradesManDTO.setFirstName(tradesMan.getFirstName());
        foundTradesManDTO.setStatus(tradesMan.getStatus());
        foundTradesManDTO.setRole(tradesMan.getRole());
        return foundTradesManDTO;
    }


}
