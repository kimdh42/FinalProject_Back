package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;

@Getter
@Setter
@AllArgsConstructor
public class OrgDetailResponse {

    private String emp_img;
    private String emp_name;
    private String dept_code;
    private String position_code;
    private String email;
    private int direct_line;

    public static OrgDetailResponse getOrgDetail(Employee employee) {

        return new OrgDetailResponse(
                employee.getEmp_img(),
                employee.getEmp_name(),
                employee.getDepartment().getDept_title(),
                employee.getPosition().getPosition_name(),
                employee.getEmail(),
                employee.getDirect_line()
        );
    }
}
