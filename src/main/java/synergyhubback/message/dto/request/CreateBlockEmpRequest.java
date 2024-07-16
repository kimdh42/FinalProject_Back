package synergyhubback.message.dto.request;

import lombok.*;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class CreateBlockEmpRequest {

    private final int blkCode;
    private final LocalDate blkDate;
    private final Employee blkId;
    private final Employee blkName;

}
