package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "APPROVAL_BOX ")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int abCode;
    private String abName;
    private int empCode;

    private ApprovalBox(String abName, int empCode){
        this.abName = abName;
        this.empCode = empCode;
    }

    public static ApprovalBox of(String abName,int empCode){
        return new ApprovalBox(abName, empCode);
    }

    public void modifyName(String abName){this.abName = abName;}
}
