package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SchoolInfoResponse {

    private int sch_code;

    private String sch_name;

    private String grad_status;

    private LocalDate enrole_date;

    private LocalDate grad_date;

    private String major;

    private String day_n_night;

    private String location;

    private int emp_code;

}
