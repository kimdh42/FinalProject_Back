package synergyhubback.calendar.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;

@Entity
@Getter
@Setter
@Table(name = "GUEST")
@NoArgsConstructor
public class Guest {

    @Id
    @Column(name = "GUEST_CODE", nullable = false, length = 255)
    private String guestCode;

    @ManyToOne
    @JoinColumn(name = "EMP_CODE", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "EVENT_CODE", nullable = true)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "TASK_CODE", nullable = true)
    private Task task;



}
