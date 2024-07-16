package synergyhubback.pheed.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDateTime;

@Entity
@Table(name = "pheed")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Pheed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pheed_code")
    private int pheedCode;           // 피드 코드

    @Column(name = "PHEED_CON")
    private String pheedCon;         // 피드내용

    @Column(name = "CRE_STATUS")
    private LocalDateTime creStatus;     // 생성시간

    @Column(name = "READ_STATUS")
    private String readStatus;       // 읽음상태

    @Column(name = "DE_STATUS")
    private String deStatus;         // 삭제상태

    @Column(name = "PHEED_SORT")
    private String pheedSort;        // 피드분류

    @ManyToOne
    @JoinColumn(name = "EMP_CODE")
    private Employee employee;       // 사원코드

    @Column(name = "URL")
    private String url;              // 링크

    @Builder
    public Pheed(int pheedCode, String pheedCon, LocalDateTime creStatus, String readStatus, String deStatus, String pheedSort, Employee employee, String url) {
        this.pheedCode = pheedCode;
        this.pheedCon = pheedCon;
        this.creStatus = creStatus;
        this.readStatus = readStatus;
        this.deStatus = deStatus;
        this.pheedSort = pheedSort;
        this.employee = employee;
        this.url = url;
    }

    public Pheed(String pheedCon, LocalDateTime creStatus, String readStatus, String deStatus, String pheedSort, Employee employee, String url) {
        this.pheedCon = pheedCon;
        this.creStatus = creStatus;
        this.readStatus = readStatus;
        this.deStatus = deStatus;
        this.pheedSort = pheedSort;
        this.employee = employee;
        this.url = url;
    }
    public static Pheed of(String pheedCon, LocalDateTime creStatus, String readStatus, String deStatus, String pheedSort, Employee employee, String url){
        return new Pheed(pheedCon, creStatus, readStatus, deStatus, pheedSort, employee, url);
    }

    /* 읽음 상태로 전환 */
    public void updateReadStatus() {
        this.readStatus = "Y";

    }

    /* 삭제 상태로 전환 */
    public void updateDeStatus() {
        this.deStatus = "Y";
    }
}
