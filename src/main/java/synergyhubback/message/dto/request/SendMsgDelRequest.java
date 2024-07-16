package synergyhubback.message.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMsgDelRequest {

    private String msgCode;
    private int storCode;
}
