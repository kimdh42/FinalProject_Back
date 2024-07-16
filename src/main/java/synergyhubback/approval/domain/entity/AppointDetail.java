package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synergyhubback.employee.domain.entity.Employee;

@Entity
@Table(name = "APPOINT_DETAIL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AppointDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adetCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aappCode")
    private ApprovalAppoint approvalAppoint;

    private String adetBefore;
    private String adetAfter;
    private String adetType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_code")
    private Employee employee;

    private AppointDetail(ApprovalAppoint approvalAppoint, String adetBefore, String adetAfter, String adetType, Employee employee){
        this.approvalAppoint = approvalAppoint;
        this.adetBefore = adetBefore;
        this.adetAfter = adetAfter;
        this.adetType = adetType;
        this.employee = employee;
    }

    public static AppointDetail of(ApprovalAppoint approvalAppoint, String adetBefore, String adetAfter, String adetType, Employee employee){
        return new AppointDetail(approvalAppoint, adetBefore, adetAfter, adetType, employee);
    }

    public void modifyAppointDetail(String adetBefore, String adetAfter, String adetType, Employee employee){
        this.adetBefore = adetBefore;
        this.adetAfter = adetAfter;
        this.adetType = adetType;
        this.employee = employee;
    }
}
