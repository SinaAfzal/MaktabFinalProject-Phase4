package ir.maktabsharif.service;


import ir.maktabsharif.model.Proposal;
import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.service.dto.request.SelectTradesManProposalDTO;
import ir.maktabsharif.service.dto.request.TaskRatingDTO;
import ir.maktabsharif.service.dto.request.TaskRequestDTO;
import ir.maktabsharif.service.dto.response.FoundProposalDTO;
import ir.maktabsharif.service.dto.response.FoundTaskDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    List<FoundTaskDTO> findTasksByStatus(TaskStatus status);//to be used only by admin


    Optional<Task> findByTaskId(Long taskId);

    /**
     * this mehtod was added during phase3 to remove security context
     */
    List<FoundTaskDTO> findTasksByStatusAndTradesManCategories(TaskStatus status, Long tradesmanId);//tobe used only by tradesman

    boolean existsById(Long taskId);

    void requestTask(TaskRequestDTO taskRequestDTO, Long customerId);//uses loggedIn user's data

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    void editTaskRequest(Long taskId, TaskRequestDTO taskRequestDTO, Long customerId);//uses loggedIn user's data

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     * AND MORE
     */
    void payTaskFromCustomerCredit(Long taskId, Long customerId);

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     * AND MORE
     */
    void payTaskUsingBankAccount(Long taskId, Long customerId);

    /**
     * this mehtod was edited heavily during phase3 to remove security context
     */
    void addCommentAndRate(TaskRatingDTO taskRatingDTO, Long customerId);//uses loggedIn user's data

    /**
     * was edited for removing security context
     */
    Float viewRatingOfDoneTask(Long taskId, Long tradesmanId);//called by tradesMan--uses loggedIn tradesMan's data

    /**
     * was edited for removing security context
     */
    void changeTaskStatusByCustomer(Long taskId, TaskStatus status, Long customerId);

    /**
     * edited by removing security context
     */
    void deleteTaskById(Long taskId, Long customerId);

    /**
     * edited by removing security context
     */
    List<FoundTaskDTO> findTasksByCustomerAndStatus(Long customerId, TaskStatus status);

    List<Task> findTasksByCustomerAndStatus_NoLogic_DirectlyCallingRepo(Long customerId, TaskStatus status);
    /**
     * edited by removing security context
     */
    List<FoundTaskDTO> findTasksByTradesManAndStatus(Long tradeManId, TaskStatus status);
    /**
     * edited by removing security context
     */
    void selectTradesManProposal(SelectTradesManProposalDTO selectPropDTO,Long customerId);//called by admin-uses logged-in admin's data

    Optional<Proposal> findWinnerProposal(Long taskId);

    ResponseDTO mapToDTO(Task task);
}
