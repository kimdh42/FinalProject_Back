package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;

@Entity
@Table(name = "TRUE_APP_LINE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TrueLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int talCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adCode")
    private Document document;

    private int talOrder;
    private String talRole;
    private String talStatus;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_code")
    private Employee employee;
    
    private String talReason;
    private LocalDate talDate;

    private TrueLine(Document document, int talOrder, String talRole, String talStatus, Employee employee){
        this.document = document;
        this.talOrder = talOrder;
        this.talRole = talRole;
        this.talStatus = talStatus;
        this.employee = employee;
    }
    public static TrueLine of(Document document, int talOrder, String talRole, String talStatus, Employee employee){
        return new TrueLine(document, talOrder, talRole, talStatus, employee);
    }

    private TrueLine(Document document, int talOrder, String talRole, String talStatus, Employee employee, LocalDate talDate){
        this.document = document;
        this.talOrder = talOrder;
        this.talRole = talRole;
        this.talStatus = talStatus;
        this.employee = employee;
        this.talDate = talDate;
    }
    public static TrueLine of(Document document, int talOrder, String talRole, String talStatus, Employee employee, LocalDate talDate){
        return new TrueLine(document, talOrder, talRole, talStatus, employee, talDate);
    }

    private TrueLine(Document document, int talOrder, String talRole, String talStatus, Employee employee, String talReason, LocalDate talDate){
        this.document = document;
        this.talOrder = talOrder;
        this.talRole = talRole;
        this.talStatus = talStatus;
        this.employee = employee;
        this.talReason = talReason;
        this.talDate = talDate;
    }
    public static TrueLine of(Document document, int talOrder, String talRole, String talStatus, Employee employee, String talReason, LocalDate talDate){
        return new TrueLine(document, talOrder, talRole, talStatus, employee, talReason, talDate);
    }

    public void modifyTrueLine(int talOrder, String talRole, String talStatus, Employee employee, String talReason, LocalDate talDate){
        this.talOrder = talOrder;
        this.talRole = talRole;
        this.talStatus = talStatus;
        this.employee = employee;
        this.talReason = talReason;
        this.talDate = talDate;
    }

    public void modifyAccept(String talStatus, LocalDate talDate){
        this.talStatus = talStatus;
        this.talDate = talDate;
    }

    public void modifyReturn(String talStatus, String talReason, LocalDate talDate){
        this.talStatus = talStatus;
        this.talReason = talReason;
        this.talDate = talDate;
    }
}
