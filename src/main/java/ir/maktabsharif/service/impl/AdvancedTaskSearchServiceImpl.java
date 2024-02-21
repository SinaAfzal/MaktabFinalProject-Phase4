package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.Task;
import ir.maktabsharif.repository.AdvancedTaskSearchDAO;
import ir.maktabsharif.service.AdvancedTaskSearchService;
import ir.maktabsharif.service.TaskService;
import ir.maktabsharif.service.dto.request.AdvancedTaskSearchDTO;
import ir.maktabsharif.service.dto.response.ResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional(readOnly = true)
public class AdvancedTaskSearchServiceImpl implements AdvancedTaskSearchService {
    private final AdvancedTaskSearchDAO advancedTaskSearchDAO;
    private final TaskService taskService;

    public AdvancedTaskSearchServiceImpl(AdvancedTaskSearchDAO advancedTaskSearchDAO, TaskService taskService) {
        this.advancedTaskSearchDAO = advancedTaskSearchDAO;
        this.taskService = taskService;
    }

    @Override
    public List<ResponseDTO> findTasks(AdvancedTaskSearchDTO advancedTaskSearchDTO) {
        List<Task> tasks = advancedTaskSearchDAO.findTasks(advancedTaskSearchDTO);
        return tasks.stream().map(taskService::mapToDTO).toList();
    }
}
