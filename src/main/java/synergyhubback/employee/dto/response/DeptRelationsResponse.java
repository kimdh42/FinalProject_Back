package synergyhubback.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.DeptRelations;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DeptRelationsResponse {

    private int dept_relations_code;

    private String par_dept_code;

    private String par_detp_title;

    private String sub_dept_code;

    private String sub_dept_title;

    public static DeptRelationsResponse fromEntity(DeptRelations deptRelations) {

        return new DeptRelationsResponse(
                deptRelations.getDept_relations_code(),
                deptRelations.getParentDepartment().getDept_code(),
                deptRelations.getParentDepartment().getDept_title(),
                deptRelations.getSubDepartment().getDept_code(),
                deptRelations.getSubDepartment().getDept_title()
        );
    }
}
