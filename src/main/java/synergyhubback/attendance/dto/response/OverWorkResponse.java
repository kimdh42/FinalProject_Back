package synergyhubback.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.attendance.domain.entity.OverWork;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class OverWorkResponse {

    private int owCode;            // 초과근무코드(pk)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate owDate;       // 초과근무 일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime owStartTime;  // 시작시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime owEndTime;    // 종료시간

    private int employee;           // 사원코드 (추후 fk)

    public OverWorkResponse(OverWork overWork) {
        this.owCode = overWork.getOwCode();
        this.owDate = overWork.getOwDate();
        this.owStartTime = overWork.getOwStartTime();
        this.owEndTime = overWork.getOwEndTime();
    }
}
