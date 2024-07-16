package synergyhubback.employee.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EmpRegistDataRequest {

    private String detailErdNum;

    private String detailErdTitle;

    private String detailErdWriter;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate detailErdRegistdate = LocalDate.now();

    private List<EmployeeRegistRequest> employees;


}
