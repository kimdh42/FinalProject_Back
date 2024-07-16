package synergyhubback.employee.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AappRegistGroupRequest {

    private String aappCode;       // 발령 결재 코드

    private String aappNo;       // 발령 번호

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate aappDate;       // 발령 일

    private String aappTitle;       // 발령 제목

    private List<AappDetailRegistRequest> appRegistDetails; // 발령 상세 정보 리스트

    public AappRegistGroupRequest() {}
}