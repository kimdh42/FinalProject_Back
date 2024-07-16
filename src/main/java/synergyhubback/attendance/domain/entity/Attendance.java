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

@Entity
@Table(name = "attendance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int atdCode;                //근태코드(pk)
    private LocalDate atdDate;          //근무날짜
    private LocalTime atdStartTime;     //지정출근시간
    private LocalTime atdEndTime;       //지정퇴근시간
    private LocalTime startTime;        //출근시간
    private LocalTime endTime;          //퇴근시간

    @ManyToOne
    @JoinColumn(name = "ats_code")
    private AttendanceStatus attendanceStatus;  //근무상태코드 (추후 fk)

    @OneToOne
    @JoinColumn(name = "ow_code")
    private OverWork overWork;          //초과근무코드

    @ManyToOne
    @JoinColumn(name = "emp_code")
    private Employee employee;          //사원코드

    @Builder
    public Attendance(int atdCode, LocalDate atdDate,
                      LocalTime atdStartTime, LocalTime atdEndTime,
                      LocalTime startTime, LocalTime endTime,
                      AttendanceStatus attendanceStatus, OverWork overWork,
                      Employee employee) {

        this.atdCode = atdCode;
        this.atdDate = atdDate;
        this.atdStartTime = atdStartTime;
        this.atdEndTime = atdEndTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendanceStatus = attendanceStatus;
        this.overWork = overWork;
        this.employee = employee;
    }

    // 지정 출근시간 업데이트
    public void updateAtdStartTime(LocalTime atdStartTime) {
        this.atdStartTime = atdStartTime;
    }

    // 지정 퇴근시간 업데이트
    public void updateAtdEndTime(LocalTime atdEndTime) {
        this.atdEndTime = atdEndTime;
    }

    // 출근시간 업데이트
    public void updateStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    // 퇴근시간 업데이트
    public void updateEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    // 근무상태 업데이트
    public void updateAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    // 초과근무 업데이트
    public void updateOverWork(OverWork overWork) {
        this.overWork = overWork;
    }

}
