package synergyhubback.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.calendar.domain.entity.Task;
import synergyhubback.calendar.domain.repository.GuestRepository;
import synergyhubback.calendar.domain.repository.TaskRepository;
import synergyhubback.calendar.dto.request.TaskCreateRequest;
import synergyhubback.calendar.dto.request.TaskUpdateRequest;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final GuestRepository guestRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    public Task createTask(TaskCreateRequest taskRequest, int empCode) {
        Task task = new Task();
        task.setId(generateTaskCode());
        task.setTitle(taskRequest.getTitle());
        task.setModifiedDate(taskRequest.getModifiedDate());
        task.setStart(taskRequest.getStart());
        task.setEnd(taskRequest.getEnd());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        task.setDescription(taskRequest.getDescription());
        task.setOwner(taskRequest.getOwner());
        task.setDisplay(taskRequest.isDisplay());

        Employee employee = employeeRepository.findById(empCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        task.setEmployee(employee);

        return taskRepository.save(task);
    }

    public Task updateTask(String id, TaskUpdateRequest taskRequest, int empCode) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(taskRequest.getTitle());
        task.setModifiedDate(taskRequest.getModifiedDate());
        task.setStart(taskRequest.getStart());
        task.setEnd(taskRequest.getEnd());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        task.setDescription(taskRequest.getDescription());
        task.setOwner(taskRequest.getOwner());
        task.setDisplay(taskRequest.isDisplay());

        Employee employee = employeeRepository.findById(empCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        task.setEmployee(employee);

        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        guestRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
    }

    public List<Task> findAllTasksByEmpCode(int empCode) {
        return taskRepository.findTasksByEmployeeOrGuest(empCode);
    }

    public List<Task> findAllTasksByEmpCodeAndStatusB(int empCode) {
        return taskRepository.findTasksByStatus(empCode);
    }



    public Optional<Task> findTaskById(String id) {
        return taskRepository.findById(id);
    }

    private String generateTaskCode() {
        Optional<Task> lastTaskOptional = taskRepository.findTopByOrderByIdDesc();
        if (lastTaskOptional.isPresent()) {
            String lastCode = lastTaskOptional.get().getId().replaceAll("[^0-9]", "");
            int lastNumber = Integer.parseInt(lastCode);
            return "TA" + (lastNumber + 1);
        } else {
            return "TA1";
        }
    }
}
