package synergyhubback.calendar.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.calendar.domain.entity.Event;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EventResponse {

    private final String id;
    private final String title;
    private final String eventCon;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime endDate;
    private final boolean allDay;
    private final String eventGuests;
    private final int empCode;
    private final long labelCode;

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getEventCon(),
                event.getStartDate(),
                event.getEndDate(),
                event.isAllDay(),
                event.getEventGuests(),
                event.getEmployee().getEmp_code(), // 사원 코드
                event.getLabel().getId() // 라벨 코드
        );
    }
}
