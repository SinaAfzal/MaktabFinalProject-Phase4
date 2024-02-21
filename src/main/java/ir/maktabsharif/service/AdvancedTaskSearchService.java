package ir.maktabsharif.service;

import ir.maktabsharif.service.dto.request.AdvancedTaskSearchDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;

import java.util.List;

public interface AdvancedTaskSearchService {
    List<ResponseDTO> findTasks(AdvancedTaskSearchDTO advancedTaskSearchDTO);
}
