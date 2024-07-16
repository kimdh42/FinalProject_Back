package synergyhubback.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventCreateRequest {

    @NotBlank
    private final String title;

    @NotBlank
    private final String eventCon;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime endDate;

    private final boolean allDay;

    private final String eventGuests;

    @NotNull
    private final int empCode; // 사원 코드

    @NotNull
    private final Long labelCode; // 라벨 코드

    @JsonCreator
    public EventCreateRequest(
            @JsonProperty("title") String title,
            @JsonProperty("eventCon") String eventCon,
            @JsonProperty("startDate") LocalDateTime startDate,
            @JsonProperty("endDate") LocalDateTime endDate,
            @JsonProperty("allDay") boolean allDay,
            @JsonProperty("eventGuests") String eventGuests,
            @JsonProperty("empCode") int empCode,
            @JsonProperty("labelCode") Long labelCode) {
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
