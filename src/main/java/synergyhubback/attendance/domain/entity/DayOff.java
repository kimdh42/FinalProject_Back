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
@Table(name = "dayoff")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class DayOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int doCode;                     //휴가기록코드(pk)
    private LocalDate doReportDate;         //신청일자
    private String doName;                  //휴가명
    private Double doUsed;                  //신청일수
    private LocalDate doStartDate;          //시작일자
    private LocalDate doEndDate;            //종료일자
    private LocalTime doStartTime;          //시작시간
    private LocalTime doEndTime;            //종료시간
    private Double granted;
    private Double dbUsed;
    private Double remaining;

    @ManyToOne
    @JoinColumn(name = "emp_code")
    private Employee employee;              //사원정보

    @Builder
    public DayOff(int doCode, LocalDate doReportDate, String doName, Double doUsed, LocalDate doStartDate, LocalDate doEndDate,
                  LocalTime doStartTime, LocalTime doEndTime, Double granted, Double dbUsed, Double remaining, Employee employee) {
        this.doCode = doCode;
        this.doReportDate = doReportDate;
        this.doName = doName;
        this.doUsed = doUsed;
        this.doStartDate = doStartDate;
        this.doEndDate = doEndDate;
        this.doStartTime = doStartTime;
        this.doEndTime = doEndTime;
        this.granted = granted;
        this.dbUsed = dbUsed;
        this.remaining = remaining;
        this.employee = employee;
    }
}
