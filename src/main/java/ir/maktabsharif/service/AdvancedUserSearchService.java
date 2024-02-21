package ir.maktabsharif.service;

import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;

import java.util.List;

public interface AdvancedUserSearchService {
    List<ResponseDTO> findUsers(AdvancedUserSearchDTO advancedUserSearchDTO);
}
