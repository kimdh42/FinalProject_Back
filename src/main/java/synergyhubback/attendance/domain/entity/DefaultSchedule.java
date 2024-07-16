package synergyhubback.attendance.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "DEFAULT_SCHEDULE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class DefaultSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ds_code")
    private int dsCode;

    @Column(name = "ds_start_date")
    private LocalDate dsStartDate;

    @Column(name = "ds_end_date")
    private LocalDate dsEndDate;

    @Column(name = "atd_start_time")
    private LocalTime atdStartTime;

    @Column(name = "atd_end_time")
    private LocalTime atdEndTime;

    @Column(name = "par_title")
    private String parTitle;

    @Column(name = "sub_title")
    private String subTitle;

    @Column(name = "dept_title")
    private String deptTitle;

    @ManyToOne
    @JoinColumn(name = "emp_Code")
    private Employee employee;          //사원코드

    @Builder
    public DefaultSchedule(int dsCode,
                           LocalDate dsStartDate, LocalDate dsEndDate,
                           String parTitle, String subTitle, String deptTitle,
                           LocalTime atdStartTime, LocalTime atdEndTime,
                           Employee employee) {
        this.dsCode = dsCode;
        this.dsStartDate = dsStartDate;
        this.dsEndDate = dsEndDate;
        this.atdStartTime = atdStartTime;
        this.atdEndTime = atdEndTime;
        this.parTitle = parTitle;
        this.subTitle = subTitle;
        this.deptTitle = deptTitle;
        this.employee = employee;
    }

    /* 지정 출퇴근시간 업데이트 */

    public void updateStartDate(LocalDate dsStartDate) {
        this.dsStartDate = dsStartDate;
    }

    public void updateEndDate(LocalDate dsEndDate) {
        this.dsEndDate = dsEndDate;
    }

    public void updateStartTime(LocalTime atdStartTime) {
        this.atdStartTime = atdStartTime;
    }

    public void updateEndTime(LocalTime atdEndTime) {
        this.atdEndTime = atdEndTime;
    }

}
