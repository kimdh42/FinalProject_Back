package synergyhubback.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TaskCreateRequest {

    private final String title;
    private final LocalDate modifiedDate;
    private final LocalDate start;
    private final LocalDate end;
    private final String status;
    private final String priority;
    private final String description;
    private final String owner;
    private final boolean display;
    private final int empCode;

    @JsonCreator
    public TaskCreateRequest(
            @JsonProperty("title") String title,
            @JsonProperty("modifiedDate") LocalDate modifiedDate,
            @JsonProperty("start") LocalDate start,
            @JsonProperty("end") LocalDate end,
            @JsonProperty("status") String status,
            @JsonProperty("priority") String priority,
            @JsonProperty("description") String description,
            @JsonProperty("owner") String owner,
            @JsonProperty("display") boolean display,
            @JsonProperty("empCode") int empCode) {
        this.title = title;
        this.modifiedDate = modifiedDate;
        this.start = start;
        this.end = end;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.owner = owner;
        this.display = display;
        this.empCode = empCode;
    }
}
