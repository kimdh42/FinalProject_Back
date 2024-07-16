package synergyhubback.common.address.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.entity.Position;

import java.time.LocalDate;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
public class AddressSelect {

    private int emp_code;
    private String emp_name;
    private String email;
    private String phone;
    private String address;
    private String account_num;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hire_date;
    private String emp_status;
    private String dept_title;
    private String position_name;
    private String title_name;

}
