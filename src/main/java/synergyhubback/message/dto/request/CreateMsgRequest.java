package synergyhubback.message.dto.request;

import lombok.*;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.message.domain.entity.Storage;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class CreateMsgRequest {

    private final String msgTitle;
    private final String msgCon;
    private final String msgStatus;
    private final String emerStatus;
    private final Employee empRev;
    private final Employee empSend;
    private final Storage revStor;
    private final Storage sendStor;
}
