package ir.maktabsharif.service.impl;


import ir.maktabsharif.model.*;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.repository.*;
import ir.maktabsharif.service.TaskService;
import ir.maktabsharif.service.TradesManService;
import ir.maktabsharif.service.dto.request.SelectTradesManProposalDTO;
import ir.maktabsharif.service.dto.request.TaskRatingDTO;
import ir.maktabsharif.service.dto.request.TaskRequestDTO;
import ir.maktabsharif.service.dto.response.FoundTaskDTO;
import ir.maktabsharif.util.Policy;
import ir.maktabsharif.util.exception.AccessDeniedException;
import ir.maktabsharif.util.exception.ExistingEntityCannotBeFetchedException;
import ir.maktabsharif.util.exception.InvalidInputException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TradesManService tradesManService;

    public TaskServiceImpl(TaskRepository repository, TradesManService tradesManService) {
        this.repository = repository;
        this.tradesManService = tradesManService;
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    @Override
    public List<FoundTaskDTO> findTasksByStatus(TaskStatus status) {
        List<Task> taskList = repository.findTasksByStatus(status);
        if (taskList.size() == 0)
            throw new NoSuchElementException("No tasks with that status was found!");
        List<FoundTaskDTO> taskDTOs = new ArrayList<>();
        for (Task t : taskList)
            taskDTOs.add(mapToDTO(t));
        return taskDTOs;
    }

    /**
     * this mehtod was added during phase3 to remove security context
     */
    @Override
    public List<FoundTaskDTO> findTasksByStatusAndTradesManCategories(TaskStatus status, Long tradesmanId) {
        List<Task> taskList = repository.findTasksByStatusAndTradesManCategories(status.toString(), tradesmanId);
        if (taskList.size() == 0)
            throw new NoSuchElementException("No tasks with that status was found!");
        List<FoundTaskDTO> taskDTOs = new ArrayList<>();
        for (Task t : taskList)
            taskDTOs.add(mapToDTO(t));
        return taskDTOs;
    }

    @Override
    public Optional<Task> findByTaskId(Long taskId) {
        return repository.findById(taskId);
    }

    @Override
    public boolean existsById(Long taskId) {
        return repository.existsById(taskId);
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    @Override
    @Transactional
    public void requestTask(TaskRequestDTO taskRequestDTO, Long customerId) {
        Customer customer = repository.findCustomerById(customerId).orElseThrow(() -> new InvalidInputException("customer not found!"));
        if (!customer.isActive())
            throw new AccessDeniedException("Only activated customer accounts are allowed to use this service!");
        Category category = repository.findCategoryByCategoryId(taskRequestDTO.getSubCategoryId()).orElseThrow(
                () -> new InvalidInputException("Please make sure the sub-category ID is correct!"));
        if (category.getParentCategory() == null)
            throw new InvalidInputException("You cannot create a task in a parent category! please select a sub-category!");
        if ( taskRequestDTO.getTaskDateAndTime().isBefore(LocalDateTime.now()))
            throw new InvalidInputException("Task time cannot be in the past!");
        Task task = Task.builder().
                status(TaskStatus.AWAITING_OFFER_BY_TRADESMEN).
                taskDateTimeByCustomer( taskRequestDTO.getTaskDateAndTime()).
                customer(customer).
                requestDateTime(LocalDateTime.now()).
                description(taskRequestDTO.getDescription()).
                locationAddress(taskRequestDTO.getLocationAddress()).
                subCategoryId(taskRequestDTO.getSubCategoryId()).
                build();
        repository.save(task);
        repository.updateCustomerNumberOfRequestedTasks(customerId, customer.getNumberOfRequestedTasks() + 1);
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    @Override
    @Transactional
    public void editTaskRequest(Long taskId, TaskRequestDTO taskRequestDTO, Long customerId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new InvalidInputException("Invalid taskId"));
        Customer customer = repository.findCustomerById(customerId).orElseThrow(() -> new InvalidInputException("customer not found!"));
        if (!task.getCustomer().getId().equals(customerId))
            throw new AccessDeniedException("Only task creator can edit the task!");
        if (!task.getStatus().equals(TaskStatus.AWAITING_OFFER_BY_TRADESMEN))
            throw new AccessDeniedException("You cannot edit the task after receiving a proposal!");
        Category category = repository.findCategoryByCategoryId(taskRequestDTO.getSubCategoryId()).orElseThrow(
                () -> new InvalidInputException("Please make sure the sub-category ID is correct!"));
        if (category.getParentCategory() == null)
            throw new InvalidInputException("You cannot create a task in a parent category! please select a sub-category!");
        if (taskRequestDTO.getTaskDateAndTime().isBefore(LocalDateTime.now()))
            throw new InvalidInputException("Task time cannot be in the past!");

        task.setTaskDateTimeByCustomer(taskRequestDTO.getTaskDateAndTime());
        task.setDescription(taskRequestDTO.getDescription());
        task.setLocationAddress(taskRequestDTO.getLocationAddress());
        task.setSubCategoryId(taskRequestDTO.getSubCategoryId());
        task.setRequestDateTime(LocalDateTime.now());
        repository.save(task);
    }

    /**
     * CAUTION!
     * To be called only at the same time when the task status is going to be changed to DONE.
     * The time of task being done should be the same when this method is called.
     * method access level was set to private as it is only used by developer in this class.
     */

    private Float calculateDelayFine(Long taskId) {
        Proposal winnerProposal = findWinnerProposal(taskId).orElseThrow(() -> new InputMismatchException("the task has no winner proposals!"));
        LocalDateTime actualFinishTime = LocalDateTime.now();
        LocalDateTime supposedFinishTime = winnerProposal.getProposedStartTime().plusHours(winnerProposal.getRequiredHours());
        float delayInMinutes = (float) MINUTES.between(actualFinishTime, supposedFinishTime);
        if (delayInMinutes <= 0F)
            return 0F;
        return Policy.getFinePerHourDelay() * delayInMinutes / 60F;
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     * AND MORE
     */
    @Override
    @Transactional
    public void payTaskFromCustomerCredit(Long taskId, Long customerId) {// todo secure method
        Task task = findByTaskId(taskId).orElseThrow(() -> new InvalidInputException("Task not found!"));
        if (!task.getCustomer().getId().equals(customerId))
            throw new InputMismatchException("Customers can only pay for their own tasks!");
        if (!task.getStatus().equals(TaskStatus.DONE))
            throw new IllegalArgumentException("Task is not done yet!");
        Customer customer = repository.findCustomerById(customerId).orElseThrow(() -> new InvalidInputException("customer not found"));
        Proposal winnerProposal = findWinnerProposal(taskId).orElseThrow(() -> new InputMismatchException("winner proposal could not be fetched!"));
        Double proposedPrice = winnerProposal.getProposedPrice();
        TradesMan tradesManWhoGotTheJob = task.getTradesManWhoGotTheJob();
        if (customer.getPurchasedBalance() < proposedPrice)
            throw new InputMismatchException("Your balance is not enough");
        Double newCustomerBalance = customer.getPurchasedBalance() - proposedPrice;
        customer.setPurchasedBalance(newCustomerBalance);
        tradesManWhoGotTheJob.setEarnedCredit(tradesManWhoGotTheJob.getEarnedCredit() + Policy.getShareOfTradesManFromPrice() * proposedPrice);
        tradesManService.saveJustForDeveloperUse(tradesManWhoGotTheJob);
        repository.updateCustomerBalance(customerId, newCustomerBalance);
        changeTaskStatusByCustomer(taskId, TaskStatus.PAID, customerId);
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     * AND MORE
     */
    @Override
    @Transactional
    public void payTaskUsingBankAccount(Long taskId, Long customerId) {//todo secure method
        Task task = findByTaskId(taskId).orElseThrow(() -> new InvalidInputException("Task not found!"));
        if (!task.getCustomer().getId().equals(customerId))
            throw new InputMismatchException("Customers can only pay for their own tasks!");
        if (!task.getStatus().equals(TaskStatus.DONE))
            throw new IllegalArgumentException("Task is not done yet!");
        Proposal winnerProposal = findWinnerProposal(taskId).orElseThrow(() -> new InputMismatchException("winner proposal could not be fetched!"));
        Double proposedPrice = winnerProposal.getProposedPrice();
        TradesMan tradesManWhoGotTheJob = task.getTradesManWhoGotTheJob();
        tradesManService.updateTradesManCreditByProposedPrice(tradesManWhoGotTheJob, proposedPrice);
        changeTaskStatusByCustomer(taskId, TaskStatus.PAID, customerId);
    }

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    @Override
    @Transactional
    public void addCommentAndRate(TaskRatingDTO taskRatingDTO, Long customerId) {
        Long taskId = taskRatingDTO.getTaskId();
        Float rating = taskRatingDTO.getRating();
        String comment = taskRatingDTO.getComment();
        Task task = repository.findById(taskId).orElseThrow(() -> new InvalidInputException("Task could not be fetched!"));
        if (!task.getStatus().equals(TaskStatus.DONE) && !task.getStatus().equals(TaskStatus.PAID))
            throw new AccessDeniedException("Task cannot be rated at this stage!");
        if (!task.getCustomer().getId().equals(customerId))
            throw new AccessDeniedException("Only task creator can rate the task!");
        task.setComment(comment);
        task.setScore(rating);
        TradesMan tradesManWhoGotTheJob = task.getTradesManWhoGotTheJob();
        Long tradesManId = tradesManWhoGotTheJob.getId();
        Float totalTradesManRating = tradesManWhoGotTheJob.getRating() + rating;
        repository.updateTradesManRating(tradesManId, totalTradesManRating);
        repository.save(task);
    }

    /**
     * was edited for removing security context
     */
    @Override
    public Float viewRatingOfDoneTask(Long taskId, Long tradesmanId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new InvalidInputException("Task cannot be fetched!"));
        if (!task.getTradesManWhoGotTheJob().getId().equals(tradesmanId))
            throw new AccessDeniedException("Only the tradesman who got the task can see the rating!");
        return task.getScore();
    }

    /**
     * was edited for removing security context
     */
    //todo use proper DTOs when you fetch an entity from database using findBy... methods!
    @Override
    @Transactional
    public void changeTaskStatusByCustomer(Long taskId, TaskStatus status, Long customerId) {
        Proposal theWinnerProposal = findWinnerProposal(taskId).orElseThrow(() -> new InputMismatchException("winner proposal does not exist or could not be fetched!"));
        Task task = repository.findById(taskId).orElseThrow(() -> new InvalidInputException("Task cannot be fetched!"));
        if (!task.getCustomer().getId().equals(customerId))
            throw new AccessDeniedException("Only creator of the task can change its status!");
        if (status.ordinal() < task.getStatus().ordinal())
            throw new InvalidInputException("You cannot change task status backwards!!!");
        if (task.getTradesManWhoGotTheJob() == null)
            throw new IllegalArgumentException("Task does not have an assigned tradesMan! Its state cannot be changed!");
        if (status.equals(TaskStatus.STARTED)) { // the customer cannot set the status to started earlier than the start time that was proposed by the chosen tradesman!
            if (task.getTradesManWhoGotTheJob() == null)
                throw new InputMismatchException("You have to select a proposal first!");
            if (LocalDateTime.now().isBefore(theWinnerProposal.getProposedStartTime()))
                throw new InputMismatchException("The proposed start time for this task has not arrived yet!");
        }

        task.setStatus(status);
        repository.save(task);
        Long numberOfDoneTasksOfCustomer = task.getCustomer().getNumberOfDoneTasks();
        //apply fines for possible delay;
        TradesMan tradesMan = task.getTradesManWhoGotTheJob();
        if (status.equals(TaskStatus.DONE)) {
            Float delayFine = calculateDelayFine(taskId);
            Float currentScoreOfTradesMan = tradesMan.getRating();
            tradesMan.setNumberOfDoneTasks(tradesMan.getNumberOfDoneTasks() + 1);
            repository.updateCustomerNumberOfDoneTasks(customerId, numberOfDoneTasksOfCustomer + 1);
            if (delayFine > currentScoreOfTradesMan) {
                tradesMan.setRating(0F);
                tradesMan.setActive(false);
            } else {
                tradesMan.setRating(currentScoreOfTradesMan - delayFine);
            }
        }
        tradesManService.saveJustForDeveloperUse(tradesMan);
    }

    /**
     * edited by removing security context
     */
    @Override
    @Transactional
    public void deleteTaskById(Long taskId, Long customerId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new InvalidInputException("Invalid taskId"));
        if (!task.getCustomer().getId().equals(customerId))
            throw new AccessDeniedException("Only task creator can delete the task!");
        if (!task.getStatus().equals(TaskStatus.AWAITING_OFFER_BY_TRADESMEN))
            throw new AccessDeniedException("You cannot delete the task after receiving a proposal!");
        repository.deleteById(taskId);
        repository.flush();//todo study flush. why did I use it?! I don't remember!
    }

    /**
     * edited by removing security context
     */
    @Override
    public List<FoundTaskDTO> findTasksByTradesManAndStatus(Long tradeManId, TaskStatus status) {
        boolean doesTradesManExist = repository.tradesmanExistsByTradesManId(tradeManId);
        if (!doesTradesManExist)
            throw new InvalidInputException("Invalid tradesman ID!");
        List<Task> taskList = repository.findTasksByWinnerTradesManAndStatus(tradeManId, status);
        if (taskList.size() == 0)
            throw new NoSuchElementException("No task of that status for this tradesMan was found!");
        List<FoundTaskDTO> taskDTOs = new ArrayList<>();
        for (Task t : taskList)
            taskDTOs.add(mapToDTO(t));
        return taskDTOs;
    }

    /**
     * edited by removing security context
     */
    @Override
    @Transactional
    public void selectTradesManProposal(SelectTradesManProposalDTO selectPropDTO, Long customerId) {
        Long proposalId = selectPropDTO.getProposalId();
        Long taskId = selectPropDTO.getTaskId();
        Proposal proposal = repository.findProposalByProposalId(proposalId).orElseThrow(() -> new InvalidInputException("proposal ID is invalid!"));
        if (!proposal.getTaskId().equals(taskId))
            throw new InputMismatchException("The proposal and the task do not match! make sure you are entering valid data and contact site admin!");
        Task task = repository.findById(taskId).orElseThrow(() -> new InvalidInputException("Invalid task ID!"));
        if (!task.getStatus().equals(TaskStatus.AWAITING_SELECTION_OF_TRADESMAN) || task.getTradesManWhoGotTheJob() != null)
            throw new AccessDeniedException("Selection of proposals at this stage is not allowed!");
        if (!task.getCustomer().getId().equals(customerId))
            throw new AccessDeniedException("Only creator of the task is allowed to select a proposal!");
        TradesMan tradesMan = repository.findTradesManByTradesManId(proposal.getTradesManId()).orElseThrow(() -> new ExistingEntityCannotBeFetchedException("The proposal does not have a properly set tradesman! please contact site admin!"));
        task.setTradesManWhoGotTheJob(tradesMan);
        task.setSelectedProposal(proposal);
        task.setStatus(TaskStatus.AWAITING_TRADESMAN_ARRIVAL);
        repository.save(task);
    }

    @Override
    public Optional<Proposal> findWinnerProposal(Long taskId) {
        Task task = findByTaskId(taskId).orElseThrow(() -> new InvalidInputException("Task not found!"));
        if (task.getTradesManWhoGotTheJob() == null)
            throw new InputMismatchException("The task has no winner proposals!");
        return repository.findWinnerProposal(taskId);
    }

    /**
     * edited by removing security context
     */
    @Override
    public List<FoundTaskDTO> findTasksByCustomerAndStatus(Long customerId, TaskStatus status) {
        boolean doesCustomerExist = repository.customerExistsByCustomerId(customerId);
        if (!doesCustomerExist)
            throw new InvalidInputException("Invalid customer ID!");
        List<Task> taskList = repository.findTasksByCustomerIdAndStatus(customerId, status);
        if (taskList.size() == 0)
            throw new NoSuchElementException("No task of that status for this customer was found!");
        List<FoundTaskDTO> taskDTOs = new ArrayList<>();
        for (Task t : taskList)
            taskDTOs.add(mapToDTO(t));
        return taskDTOs;
    }

    @Override
    public List<Task> findTasksByCustomerAndStatus_NoLogic_DirectlyCallingRepo(Long customerId, TaskStatus status) {
        return repository.findTasksByCustomerIdAndStatus(customerId, status);
    }

    @Override
    @Transactional
    public FoundTaskDTO mapToDTO(Task task) {
        FoundTaskDTO foundTaskDTO = new FoundTaskDTO();
        foundTaskDTO.setId(task.getId());
        foundTaskDTO.setCustomerId(task.getCustomer().getId());
        foundTaskDTO.setDescription(task.getDescription());
        foundTaskDTO.setDateTimeOfBeingDone(task.getDateTimeOfBeingDone());
        foundTaskDTO.setTaskStatus(task.getStatus());
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof TradesMan)
            foundTaskDTO.setComment("TradesMen are not allowed to see this!");
        else
            foundTaskDTO.setComment(task.getComment());
        foundTaskDTO.setLocationAddress(task.getLocationAddress());
        foundTaskDTO.setScore(task.getScore());
        if (task.getTradesManWhoGotTheJob() != null) {
            foundTaskDTO.setTradesManWhoGotTheJobId(task.getTradesManWhoGotTheJob().getId());
            foundTaskDTO.setSelectedProposalId(task.getSelectedProposal().getId());
            foundTaskDTO.setWinnerPrice(task.getSelectedProposal().getProposedPrice());
        }
        foundTaskDTO.setTaskDateTimeByCustomer(task.getTaskDateTimeByCustomer());
        foundTaskDTO.setSubCategoryId(task.getSubCategoryId());
        foundTaskDTO.setRequestDateTime(task.getRequestDateTime());
        return foundTaskDTO;
    }

}
