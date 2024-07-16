package synergyhubback.employee.domain.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "department")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Department {

    @Id
    private String dept_code;

    private String dept_title;

    private LocalDate creation_date;

    private LocalDate end_date;

    // 하위 부서 관계 설정
    @OneToMany(mappedBy = "parentDepartment", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<DeptRelations> subDepartments = new HashSet<>();

    // 상위 부서 관계 설정
    @OneToMany(mappedBy = "subDepartment", fetch = FetchType.LAZY)
    private Set<DeptRelations> parentDepartments = new HashSet<>();


    public Department(String dept_code, String dept_title, LocalDate creation_date, LocalDate end_date, Set<DeptRelations> parentDepartments, Set<DeptRelations> subDepartments) {
        this.dept_code = dept_code;
        this.dept_title = dept_title;
        this.creation_date = creation_date;
        this.end_date = end_date;
        this.parentDepartments = parentDepartments;
        this.subDepartments = subDepartments;
    }


    public void setParentDepartment(Department parentDepartment) {
        // 기존의 parentDepartments를 업데이트할 수도 있어야 함
        this.parentDepartments.add(new DeptRelations(parentDepartment, this));
    }

    public void addSubDepartment(Department subDepartment) {
        this.subDepartments.add(new DeptRelations(this, subDepartment));
    }
}
