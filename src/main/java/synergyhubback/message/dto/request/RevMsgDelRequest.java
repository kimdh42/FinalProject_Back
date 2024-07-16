package synergyhubback.message.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevMsgDelRequest {

    private String msgCode;
    private int storCode;

}
