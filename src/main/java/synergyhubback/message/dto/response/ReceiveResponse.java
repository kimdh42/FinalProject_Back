package synergyhubback.message.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.message.domain.entity.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReceiveResponse {

    private String msgCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sendDate;
    private String msgTitle;
    private String msgCon;
    private String msgStatus;
    private String emerStatus;
    private int empRev;
    private int empSend;
    private int revStor;
    private int sendStor;
    private String sendName;
    private String sendPosition;

    public static ReceiveResponse getReceiveMessage(Message message) {

        return new ReceiveResponse(
                message.getMsgCode(),
                message.getSendDate(),
                message.getMsgTitle(),
                message.getMsgCon(),
                message.getMsgStatus(),
                message.getEmerStatus(),
                message.getEmpRev().getEmp_code(),
                message.getEmpSend().getEmp_code(),
                message.getRevStor().getStorCode(),
                message.getSendStor().getStorCode(),
                message.getEmpSend().getEmp_name(),
                message.getEmpSend().getPosition().getPosition_name()
        );
    }
}
