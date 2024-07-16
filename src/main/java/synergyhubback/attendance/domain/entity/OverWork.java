package synergyhubback.attendance.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "overwork")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class OverWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ow_code")
    private int owCode;            // 초과근무코드(pk)

    @Column(name = "ow_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate owDate;       // 초과근무 일자

    @Column(name = "ow_start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime owStartTime;  // 시작시간

    @Column(name = "ow_end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime owEndTime;    // 종료시간

    @ManyToOne
    @JoinColumn(name = "emp_code")
    private Employee employee;      // 사원코드 (추후 fk)

    @Builder
    public OverWork(int owCode, LocalDate owDate, LocalTime owStartTime, LocalTime owEndTime, Employee employee) {
        this.owCode = owCode;
        this.owDate = owDate;
        this.owStartTime = owStartTime;
        this.owEndTime = owEndTime;
        this.employee = employee;
    }

}
