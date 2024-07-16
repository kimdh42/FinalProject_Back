package synergyhubback.employee.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class EmployeeRegistRequest {

    private final int emp_code;

    private int par_emp_code;

    @NotBlank
    private String emp_name;

    private String emp_pass;

    @NotBlank
    private String social_security_no;

    @NotBlank
    private String email;

    @NotBlank
    private String dept_code;

    @NotBlank
    private String position_code;

    @NotBlank
    private String title_code;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate hire_date;

    private String emp_status;

    private String detailErdNum;

    private String detailErdTitle;

    private String detailErdWriter;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate detailErdRegistdate;

}
