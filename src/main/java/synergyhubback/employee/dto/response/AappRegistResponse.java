package synergyhubback.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AappRegistResponse {

    private String aappCode;       // 발령 결재 코드

    private String aappNo;       // 발령 번호

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate aappDate;       // 발령 일

    private String aappTitle;       // 발령 제목

    public AappRegistResponse() {}
}