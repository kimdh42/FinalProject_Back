package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.Employee;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetDeptTitleResponse {

    private String dept_code;

    private String dept_title;

    private List<String> subDeptCodes;

    private List<String> parDeptCodes;

}
