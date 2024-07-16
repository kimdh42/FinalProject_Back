package synergyhubback.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class RegistDeptRequest {


    private String dept_title;


    public RegistDeptRequest() {}
}
