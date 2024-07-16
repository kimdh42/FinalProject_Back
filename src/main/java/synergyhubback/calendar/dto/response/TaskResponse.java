package synergyhubback.calendar.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.calendar.domain.entity.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskResponse {

    private final String id;
    private final String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate modifiedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate end;
    private final String status;
    private final String priority;
    private final String description;
    private final String owner;
    private final boolean display;
    private final int empCode;

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getModifiedDate(),
                task.getStart(),
                task.getEnd(),
                task.getStatus(),
                task.getPriority(),
                task.getDescription(),
                task.getOwner(),
                task.isDisplay(),
                task.getEmployee().getEmp_code()
        );
    }
}
