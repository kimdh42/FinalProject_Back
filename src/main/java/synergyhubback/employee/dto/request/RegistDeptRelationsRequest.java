package synergyhubback.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Department;

@Getter
@Setter
@AllArgsConstructor
public class RegistDeptRelationsRequest {


    private int dept_relations_code;

    private Department parentDepartment;

    private Department subDepartment;

    public RegistDeptRelationsRequest() {}
}
