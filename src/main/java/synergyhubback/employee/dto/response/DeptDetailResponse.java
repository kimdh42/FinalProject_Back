package synergyhubback.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Department;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class DeptDetailResponse {

    private String dept_code;

    private String dept_manager_name;

    private String dept_title;

    private List<String> par_dept_title;        // 상위 부서

    private List<String> sub_dept_title;        // 하위 부서

    private int numOfTeamMember;        // 팀원 수

    private List<String> teamMemberName;        //팀원 이름

    private String parentDeptManagerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creation_date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end_date;


    public static DeptDetailResponse getDeptDetail(Department department, String deptManagerName, List<String> parDeptTitles, List<String> subDeptTitles, int numOfTeamMember, List<String> teamMemberName, String parentDeptManagerName) {

        return new DeptDetailResponse(

                department.getDept_code(),
                deptManagerName,
                department.getDept_title(),
                parDeptTitles,
                subDeptTitles,
                numOfTeamMember,
                teamMemberName,
                parentDeptManagerName,
                department.getCreation_date(),
                department.getEnd_date()

        );
    }
}
