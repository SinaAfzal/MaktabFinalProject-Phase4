package ir.maktabsharif.service.impl;


import ir.maktabsharif.model.*;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.repository.ProposalRepository;
import ir.maktabsharif.service.ProposalService;
import ir.maktabsharif.service.TradesManService;
import ir.maktabsharif.service.dto.request.TaskProposalDTO;
import ir.maktabsharif.service.dto.response.FoundProposalDTO;
import ir.maktabsharif.util.exception.AccessDeniedException;
import ir.maktabsharif.util.exception.ExistingEntityCannotBeFetchedException;
import ir.maktabsharif.util.exception.InvalidInputException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProposalServiceImpl implements ProposalService {
    private final ProposalRepository repository;
    private final TradesManService tradesManService;


    public ProposalServiceImpl(ProposalRepository repository, TradesManService tradesManService) {
        this.repository = repository;
        this.tradesManService = tradesManService;
    }

    @Override
    public List<FoundProposalDTO> findByTradesManId(Long tradesManId) {
        boolean existsById = repository.tradesmanExistsByTradesmanId(tradesManId);

        if (!existsById)
            throw new InvalidInputException("TradesMan could not be found!");
        List<Proposal> proposalList = repository.findByTradesManId(tradesManId);
        if (proposalList.size() == 0)
            throw new NoSuchElementException("TradesMan has no proposals!");
        List<FoundProposalDTO> proposalDTOs = new ArrayList<>();
        for (Proposal p : proposalList)
            proposalDTOs.add(mapToDTO(p));
        return proposalDTOs;
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    @Override
    @Transactional
    public void createProposal(Long tradesmanId, TaskProposalDTO tskProDTO) { //todo each tradesMan should not be able to create more than 1 proposal for each task!
        TradesMan tradesMan = tradesManService.findById_ForDevelopmentOnly(tradesmanId);
        if (!tradesMan.isActive())
            throw new AccessDeniedException("Your account is not approved or has been deactivated! please contact site admin!");
        if (repository.didTradesmanSendProposalForTaskBefore(tradesmanId,tskProDTO.getTaskId()))
            throw new IllegalCallerException("You cannot send multiple proposals for the same task!");
        Task task = repository.findTaskByTaskId(tskProDTO.getTaskId()).orElseThrow(()-> new InvalidInputException("Task could not be fetched!"));
        if (!task.getStatus().equals(TaskStatus.AWAITING_OFFER_BY_TRADESMEN) && !task.getStatus().equals(TaskStatus.AWAITING_SELECTION_OF_TRADESMAN))
            throw new AccessDeniedException("Task is not accepting proposals at this stage!");
        Category category=repository.findCategoryByCategoryId(task.getSubCategoryId()).orElseThrow(()->new InputMismatchException("Task has no category! please contact site admin!"));
        if (category.getBasePrice() > tskProDTO.getProposedPrice())
            throw new InvalidInputException("The proposed price cannot be less than sub-category's base price!");
        if (tskProDTO.getProposedStartTime().isBefore(LocalDateTime.now()) || tskProDTO.getProposedStartTime().isBefore(task.getTaskDateTimeByCustomer()))
            throw new InvalidInputException("The proposed date/time cannot be earlier than current server time or the time determined by customer!");

        task.setStatus(TaskStatus.AWAITING_SELECTION_OF_TRADESMAN);
        repository.save(mapToEntity(tskProDTO,tradesmanId));
        tradesMan.setNumberOfProposalsSent(tradesMan.getNumberOfProposalsSent() + 1);
        tradesManService.saveJustForDeveloperUse(tradesMan);
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    @Override
    public List<FoundProposalDTO> findByTaskId(Long taskId, Long cId) {
        Optional<Task> taskOptional = repository.findTaskByTaskId(taskId);
        if (taskOptional.isEmpty())
            throw new InvalidInputException("Task not found or could not be fetched! Make sure the task Id is valid!");
        if (!taskOptional.get().getCustomer().getId().equals(cId))
            throw new InputMismatchException("Customers can only see proposals of their own task!");
        List<Proposal> proposalList = repository.findByTaskId(taskId);
        if (proposalList.size() == 0)
            throw new NoSuchElementException("There are no proposals for this task yet!");
        List<FoundProposalDTO> proposalDTOs = new ArrayList<>();
        for (Proposal p : proposalList)
            proposalDTOs.add(mapToDTO(p));
        return proposalDTOs;
    }

    @Override
    public List<FoundProposalDTO> findByTaskId(Long taskId) {
        List<Proposal> proposalList = repository.findByTaskId(taskId);
        if (proposalList.size() == 0)
            throw new NoSuchElementException("There are no proposals for this task yet!");
        List<FoundProposalDTO> proposalDTOs = new ArrayList<>();
        for (Proposal p : proposalList)
            proposalDTOs.add(mapToDTO(p));
        return proposalDTOs;
    }

    @Override
    public List<FoundProposalDTO> findProposalsByTaskIdSortByTradesManScoreAscending(Long taskId, Long cId) {
        List<FoundProposalDTO> list = findByTaskId(taskId, cId);
        return list.stream()
                .sorted((p1, p2) ->
                        Float.compare(
                                repository.findTradesmanByTradesManId(p1.getTradesManId()).get().getRating()
                                , repository.findTradesmanByTradesManId(p2.getTradesManId()).get().getRating()
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public List<FoundProposalDTO> findProposalsByTaskIdSortByTradesManScoreAscendingWithQuery(Long taskId, Long customerId) {
        Optional<Task> taskOptional = repository.findTaskByTaskId(taskId);
        if (taskOptional.isEmpty())
            throw new InvalidInputException("Task not found or could not be fetched! Make sure the task Id is valid!");
        if (!taskOptional.get().getCustomer().getId().equals(customerId))
            throw new InputMismatchException("Customers can only see proposals of their own task!");
        List<Proposal> proposalList = repository.findProposalsByTaskIdSortByTradesManScoreAscending(taskId);
        if (proposalList.size() == 0)
            throw new NoSuchElementException("There are no proposals for this task yet!");
        List<FoundProposalDTO> proposalDTOs = new ArrayList<>();
        for (Proposal p : proposalList)
            proposalDTOs.add(mapToDTO(p));
        return proposalDTOs;
    }

    @Override
    public List<FoundProposalDTO> findProposalsByTaskIdSortByProposedPriceAscending(Long taskId, Long cId) {
        List<FoundProposalDTO> list = findByTaskId(taskId, cId);
        return list.stream()
                .sorted(Comparator.comparingDouble(FoundProposalDTO::getProposedPrice))
                .collect(Collectors.toList());
    }

    @Override
    public List<FoundProposalDTO> findProposalsByTaskIdSortByProposedPriceAscendingWithQuery(Long taskId, Long customerId) {
        Optional<Task> taskOptional = repository.findTaskByTaskId(taskId);
        if (taskOptional.isEmpty())
            throw new InvalidInputException("Task not found or could not be fetched! Make sure the task Id is valid!");
        if (!taskOptional.get().getCustomer().getId().equals(customerId))
            throw new InputMismatchException("Customers can only see proposals of their own task!");
        List<Proposal> proposalList = repository.findProposalsByTaskIdSortByProposedPriceAscending(taskId);
        if (proposalList.size() == 0)
            throw new NoSuchElementException("There are no proposals for this task yet!");
        List<FoundProposalDTO> proposalDTOs = new ArrayList<>();
        for (Proposal p : proposalList)
            proposalDTOs.add(mapToDTO(p));
        return proposalDTOs;
    }

    @Override
    public List<FoundProposalDTO> findProposalsByTaskIdSortByTradesManScoreAscending(Long taskId) {
        List<FoundProposalDTO> list = findByTaskId(taskId);
        return list.stream()
                .sorted((p1, p2) ->
                        Float.compare(
                                repository.findTradesmanByTradesManId(p1.getTradesManId()).get().getRating()
                                , repository.findTradesmanByTradesManId(p2.getTradesManId()).get().getRating()
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public List<FoundProposalDTO> findProposalsByTaskIdSortByProposedPriceAscending(Long taskId) {
        List<FoundProposalDTO> list = findByTaskId(taskId);
        return list.stream()
                .sorted(Comparator.comparingDouble(FoundProposalDTO::getProposedPrice))
                .collect(Collectors.toList());
    }


    @Override
    public List<Proposal> findByTaskId_NoLogicDirectCallingFromRepo(Long taskId) {
        return repository.findByTaskId(taskId);
    }

    @Override
    public void deleteProposalById(Long proposalId, Long tradesmanId) {
        if (!repository.existsById(proposalId))
            throw new IllegalArgumentException("proposal not found!");
        Proposal proposal = repository.findById(proposalId).orElseThrow(() -> new ExistingEntityCannotBeFetchedException("proposal exists but cannot be fetched!"));
        if (!proposal.getTradesManId().equals(tradesmanId))
            throw new AccessDeniedException("Only proposal owner and an admin can access this service!");
        repository.deleteById(proposalId);
    }

    private Proposal mapToEntity(TaskProposalDTO dto,Long tradesmanId){
        return Proposal.builder().
                proposalRegistrationTime(LocalDateTime.now()).
                proposedStartTime(dto.getProposedStartTime()).
                proposedPrice(dto.getProposedPrice()).
                requiredHours(dto.getRequiredHours()).
                tradesManId(tradesmanId).
                taskId(dto.getTaskId()).build();
    }

    @Override
    public FoundProposalDTO mapToDTO(Proposal proposal) {
        FoundProposalDTO foundProposalDTO = new FoundProposalDTO();
        foundProposalDTO.setProposedPrice(proposal.getProposedPrice());
        foundProposalDTO.setProposalRegistrationTime(proposal.getProposalRegistrationTime());
        foundProposalDTO.setProposedStartTime(proposal.getProposedStartTime());
        foundProposalDTO.setId(proposal.getId());
        foundProposalDTO.setTaskId(proposal.getTaskId());
        foundProposalDTO.setRequiredHours(proposal.getRequiredHours());
        foundProposalDTO.setTradesManId(proposal.getTradesManId());
        return foundProposalDTO;
    }
}
