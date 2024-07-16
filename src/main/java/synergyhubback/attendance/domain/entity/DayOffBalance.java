package synergyhubback.attendance.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;

@Entity
@Table(name = "dayoff_balance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class DayOffBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dbCode;                 //휴가관리코드(pk)
    private Double granted;                //부여수
    private Double remaining;              //잔여수
    private Double dbUsed;                 //사용수

    @ManyToOne
    @JoinColumn(name = "emp_code")
    private Employee employee;          //사원코드

    private LocalDate dbInsertDate;     //부여일자

    @Builder
    public DayOffBalance(int dbCode, Double granted, Double remaining, Double dbUsed, Employee employee, LocalDate dbInsertDate) {
        this.dbCode = dbCode;
        this.granted = granted;
        this.remaining = remaining;
        this.dbUsed = dbUsed;
        this.employee = employee;
        this.dbInsertDate = dbInsertDate;
    }

    /* 수정 메소드 */

    // 부여수
    public void modifyGranted(double granted) {
        this.granted -= granted;
    }

    // 사용수
    public void modifyDbUsed(double dbUsed) {
        this.dbUsed += dbUsed;
    }

    // 잔여수
    public void modifyRemaining(double remaining) {
        this.remaining -= remaining;
    }

}
