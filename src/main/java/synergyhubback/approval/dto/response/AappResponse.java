package synergyhubback.approval.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.AppointDetail;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AappResponse {
    private final String aappCode;
    private final String aappNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate aappDate;
    private final String aappTitle;

    private final String adetBefore;
    private final String adetAfter;
    private final String adetType;

    private final int emp_code;
    private final String emp_name;

    public static AappResponse from(final AppointDetail appointDetail){
        return new AappResponse(
                appointDetail.getApprovalAppoint().getAappCode(),
                appointDetail.getApprovalAppoint().getAappNo(),
                appointDetail.getApprovalAppoint().getAappDate(),
                appointDetail.getApprovalAppoint().getAappTitle(),
                appointDetail.getAdetBefore(),
                appointDetail.getAdetAfter(),
                appointDetail.getAdetType(),
                appointDetail.getEmployee().getEmp_code(),
                appointDetail.getEmployee().getEmp_name()
        );
    }
}
