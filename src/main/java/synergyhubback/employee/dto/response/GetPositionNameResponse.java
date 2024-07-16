package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.entity.Position;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class GetPositionNameResponse {

    private String position_code;

    private String position_name;


    public static GetPositionNameResponse from(Position position) {

        return new GetPositionNameResponse(

                position.getPosition_code(),
                position.getPosition_name()

        );
    }
}
