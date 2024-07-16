package synergyhubback.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Certificate;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.entity.SchoolInfo;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MyInfoResponse {

    private int emp_code;
    private String emp_name;
    private String emp_pass;
    private String social_security_no;
    private String email;
    private String phone;
    private String address;
    private int direct_line;
    private String account_num;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate hire_date;
    private LocalDate end_date;
    private String emp_status;
    private Integer emp_sign;
    private String emp_img;
    private String dept_code;
    private String dept_title;
    private String title_code;
    private String title_name;
    private String position_code;
    private String position_name;
    private int bank_code;
    private String bank_name;


    public static MyInfoResponse getMyInfo(Employee employee) {

        return new MyInfoResponse(
                employee.getEmp_code(),
                employee.getEmp_name(),
                employee.getEmp_pass(),
                employee.getSocial_security_no(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAddress(),
                employee.getDirect_line(),
                employee.getAccount_num(),
                employee.getHire_date(),
                employee.getEnd_date(),
                employee.getEmp_status(),
                employee.getEmp_sign(),
                employee.getEmp_img(),
                employee.getDepartment().getDept_code(),
                employee.getDepartment().getDept_title(),
                employee.getTitle().getTitle_code(),
                employee.getTitle().getTitle_name(),
                employee.getPosition().getPosition_code(),
                employee.getPosition().getPosition_name(),
                employee.getBank().getBank_code(),
                employee.getBank().getBank_name()
        );
    }


}
