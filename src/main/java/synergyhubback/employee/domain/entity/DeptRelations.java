package synergyhubback.employee.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "dept_relations")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeptRelations {

    @Id
    private int dept_relations_code;

    @ManyToOne
    @JoinColumn(name = "PAR_DEPT_CODE")
    private Department parentDepartment;

    @ManyToOne
    @JoinColumn(name = "SUB_DEPT_CODE")
    private Department subDepartment;

    public DeptRelations(Department parentDepartment, Department subDepartment) {
        this.parentDepartment = parentDepartment;
        this.subDepartment = subDepartment;
    }

    public void setParentDepartment(Department parentDepartment) {
        this.parentDepartment = parentDepartment;
    }

    public void setSubDepartment(Department subDepartment) {
        this.subDepartment = subDepartment;
    }

    public String getPar_dept_code() {
        return parentDepartment.getDept_code();
    }

    public String getSub_dept_code() {
        return subDepartment.getDept_code();
    }
}
