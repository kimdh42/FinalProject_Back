package synergyhubback.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String eventCon;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private boolean allDay;

    private String eventGuests;

    @NotNull
    private int empCode; // 사원 코드

    @NotNull
    private Long labelCode; // 라벨 코드

    @JsonCreator
    public EventUpdateRequest(
            @JsonProperty("title") String title,
            @JsonProperty("eventCon") String eventCon,
            @JsonProperty("startDate") LocalDateTime startDate,
            @JsonProperty("endDate") LocalDateTime endDate,
            @JsonProperty("allDay") boolean allDay,
            @JsonProperty("eventGuests") String eventGuests,
            @JsonProperty("empCode") int empCode,
            @JsonProperty("labelCode") Long labelCode
    ) {
        this.title = title;
        this.eventCon = eventCon;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allDay = allDay;
        this.eventGuests = eventGuests;
        this.empCode = empCode;
        this.labelCode = labelCode;
    }
}
