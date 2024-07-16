package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CertificateResponse {

    private int cer_code;

    private String cer_name;

    private String cer_score;

    private LocalDate cer_date;

    private String cer_num;

    private String iss_organ;

    private int emp_code;

}
