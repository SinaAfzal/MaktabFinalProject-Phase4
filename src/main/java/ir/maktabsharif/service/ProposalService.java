package ir.maktabsharif.service;


import ir.maktabsharif.model.Proposal;
import ir.maktabsharif.service.dto.request.TaskProposalDTO;
import ir.maktabsharif.service.dto.response.FoundProposalDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;


import java.util.List;

public interface ProposalService  {

    List<FoundProposalDTO> findByTradesManId(Long tradesManId);
    /**this mehtod was edited heavily during phase3 to remove security context*/
    void createProposal(Long tradesmanId,TaskProposalDTO tskProDTO);//uses logged-in user's data
    /**this mehtod was edited heavily during phase3 to remove security context*/
    List<FoundProposalDTO> findByTaskId(Long taskId);
    List<FoundProposalDTO> findProposalsByTaskIdSortByTradesManScoreAscending(Long taskId);//todo sort directly by query because that way is faster.(Also compare the speed)
    List<FoundProposalDTO> findProposalsByTaskIdSortByProposedPriceAscending(Long taskId);//todo sort directly by query because that way is faster.(Also compare the speed)
    List<Proposal> findByTaskId_NoLogicDirectCallingFromRepo(Long taskId);
    void deleteProposalById(Long proposalId,Long tradesmanId);
    ResponseDTO mapToDTO(Proposal proposal);

}
