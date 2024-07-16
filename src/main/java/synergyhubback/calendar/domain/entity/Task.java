package synergyhubback.calendar.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "TASK")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @Column(name = "TASK_CODE")
    private String id;

    @Column(name = "TASK_TITLE")
    private String title;

    @Column(name = "TASK_MODI_DATE")
    private LocalDate modifiedDate;

    @Column(name = "START_DATE")
    private LocalDate start;

    @Column(name = "END_DATE")
    private LocalDate end;

    @Column(name = "TASK_STATUS")
    private String status;

    @Column(name = "TASK_PRI")
    private String priority;

    @Column(name = "TASK_CON")
    private String description;

    @Column(name = "TASK_OWNER")
    private String owner;

    @Column(name = "TASK_DISPLAY")
    private boolean display;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_CODE")
    private Employee employee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Guest> guests;
}
