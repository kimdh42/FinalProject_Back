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
public class TempResponse {

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
    private String revName;
    private String revPosition;

    public static TempResponse getTempMessage(Message message) {

        return new TempResponse(
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
                message.getEmpRev().getEmp_name(),
                message.getEmpRev().getPosition().getPosition_name()
        );
    }

}
