package synergyhubback.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.DetailByEmpRegist;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class EmpRegistDetailResponse {

    private int emp_code;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate hire_date;

    private String emp_name;

    private String social_security_no;

    private String email;

    private String dept_title;

    private String position_name;

    private String title_name;

    public static EmpRegistDetailResponse fromEntity(DetailByEmpRegist detailByEmpRegist) {
        return new EmpRegistDetailResponse(
                detailByEmpRegist.getEmployee().getEmp_code(),
                detailByEmpRegist.getEmployee().getHire_date(),
                detailByEmpRegist.getEmployee().getEmp_name(),
                detailByEmpRegist.getEmployee().getSocial_security_no(),
                detailByEmpRegist.getEmployee().getEmail(),
                detailByEmpRegist.getEmployee().getDepartment().getDept_title(),
                detailByEmpRegist.getEmployee().getPosition().getPosition_name(),
                detailByEmpRegist.getEmployee().getTitle().getTitle_name()
        );
    }

}
