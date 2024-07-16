package synergyhubback.calendar.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EVENT")
public class Event {

    @Id
    @Column(name = "EVENT_CODE")
    private String id;

    @Column(name = "EVENT_TITLE")
    private String title;

    @Column(name = "EVENT_CON")
    private String eventCon;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "ALLDAY")
    private boolean allDay;

    @Column(name = "EVENT_GUESTS")
    private String eventGuests;

    @ManyToOne
    @JoinColumn(name = "EMP_CODE")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "LABEL_CODE")
    private Label label;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Guest> guests;

    public static Event createEvent(
            String title,
            String eventCon,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean allDay,
            String eventGuests,
            Employee employee,
            Label label
    ) {
        Event event = new Event();
        event.title = title;
        event.eventCon = eventCon;
        event.startDate = startDate;
        event.endDate = endDate;
        event.allDay = allDay;
        event.eventGuests = eventGuests;
        event.employee = employee;
        event.label = label;
        return event;
    }
}
