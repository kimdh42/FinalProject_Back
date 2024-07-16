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
public class DetailByEmpRegistResponse {

    private int erd_code;

    private String erd_num;

    private String erd_title;

    private String erd_writer;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate erd_registdate;

    private int emp_code;

    public static DetailByEmpRegistResponse getEmpRegistList(DetailByEmpRegist detailByEmpRegist) {

        return new DetailByEmpRegistResponse(

                detailByEmpRegist.getErd_code(),
                detailByEmpRegist.getErd_num(),
                detailByEmpRegist.getErd_title(),
                detailByEmpRegist.getErd_writer(),
                detailByEmpRegist.getErd_registdate(),
                detailByEmpRegist.getEmployee().getEmp_code()
        );
    }
}
