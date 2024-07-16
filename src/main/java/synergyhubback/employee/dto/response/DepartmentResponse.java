package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DepartmentResponse {

    private List<DeptDetailResponse> departmentList;

    public static DepartmentResponse from(List<DeptDetailResponse> departmentList) {
        return new DepartmentResponse(departmentList);
    }

}
