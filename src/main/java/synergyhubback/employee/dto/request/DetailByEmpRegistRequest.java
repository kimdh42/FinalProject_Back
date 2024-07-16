package synergyhubback.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DetailByEmpRegistRequest {

    private String erd_num;

    private String erd_title;

    private String erd_writer;

    private LocalDate erd_registdate;

}
