package synergyhubback.calendar.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synergyhubback.calendar.domain.entity.Task;
import synergyhubback.calendar.dto.request.TaskCreateRequest;
import synergyhubback.calendar.dto.request.TaskUpdateRequest;
import synergyhubback.calendar.dto.response.TaskResponse;
import synergyhubback.calendar.service.TaskService;
import synergyhubback.auth.util.TokenUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/task/regist")
    public ResponseEntity<TaskResponse> createTask(@RequestHeader("Authorization") String token, @RequestBody TaskCreateRequest taskRequest) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            Task task = taskService.createTask(taskRequest, empCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<TaskResponse> updateTask(@RequestHeader("Authorization") String token, @PathVariable String id, @RequestBody TaskUpdateRequest taskRequest) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            Task task = taskService.updateTask(id, taskRequest, empCode);
            return ResponseEntity.ok(TaskResponse.from(task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<Void> deleteTask(@RequestHeader("Authorization") String token, @PathVariable String id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            List<TaskResponse> tasks = taskService.findAllTasksByEmpCode(empCode).stream().map(TaskResponse::from).collect(Collectors.toList());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@RequestHeader("Authorization") String token, @PathVariable String id) {
        try {
            return taskService.findTaskById(id)
                    .map(task -> ResponseEntity.ok(TaskResponse.from(task)))
                    .orElseThrow(() -> new RuntimeException("Task not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
