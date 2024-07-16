package synergyhubback.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TaskUpdateRequest {

    @JsonProperty("id")
    private String id; // 추가된 id 필드

    @JsonProperty("title")
    private String title;

    @JsonProperty("modifiedDate")
    private LocalDate modifiedDate;

    @JsonProperty("start")
    private LocalDate start;

    @JsonProperty("end")
    private LocalDate end;

    @JsonProperty("status")
    private String status;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("description")
    private String description;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("display")
    private boolean display;

    @JsonProperty("empCode")
    private int empCode;

    @JsonCreator
    public TaskUpdateRequest(
            @JsonProperty("id") String id, // 수정된 생성자
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
        this.id = id;
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
