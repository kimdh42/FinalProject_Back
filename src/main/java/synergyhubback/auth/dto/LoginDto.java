package synergyhubback.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.employee.domain.entity.Employee;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginDto {

    private final int emp_code;
    private final String emp_name;
    private final String emp_pass;

    public static LoginDto from(Employee employee) {
        return new LoginDto(
                employee.getEmp_code(),
                employee.getEmp_name(),
                employee.getEmp_pass()
        );
    }

}
