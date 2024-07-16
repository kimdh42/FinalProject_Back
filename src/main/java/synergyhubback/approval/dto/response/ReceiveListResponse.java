package synergyhubback.approval.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.TrueLine;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReceiveListResponse {
    private final String adCode;
    private final String adTitle;
    private final int emp_code;
    private final String emp_name;
    private final String dept_title;
    private final String title_name;
    @JsonFormat(pattern = "yyyy.MM.dd")
    private final LocalDate adReportDate;
    private final String adStatus;
    private final int afCode;
    private final String afName;
    private final int lsCode;
    private final String adDetail;
    private final int talOrder;
    private final String talRole;
    private final String talStatus;
    @JsonFormat(pattern = "yyyy.MM.dd")
    private final LocalDate talDate;
    private final int empCode;
    private final String empName;
    private final String talReason;

    public static ReceiveListResponse from(final TrueLine trueLine){
        return new ReceiveListResponse(
                trueLine.getDocument().getAdCode(),
                trueLine.getDocument().getAdTitle(),
                trueLine.getDocument().getEmployee().getEmp_code(),
                trueLine.getDocument().getEmployee().getEmp_name(),
                trueLine.getDocument().getEmployee().getDepartment().getDept_title(),
                trueLine.getDocument().getEmployee().getTitle().getTitle_name(),
                trueLine.getDocument().getAdReportDate(),
                trueLine.getDocument().getAdStatus(),
                trueLine.getDocument().getForm().getAfCode(),
                trueLine.getDocument().getForm().getAfName(),
                trueLine.getDocument().getForm().getLineSort().getLsCode(),
                trueLine.getDocument().getAdDetail(),
                trueLine.getTalOrder(),
                trueLine.getTalRole(),
                trueLine.getTalStatus(),
                trueLine.getTalDate(),
                trueLine.getEmployee().getEmp_code(),
                trueLine.getEmployee().getEmp_name(),
                trueLine.getTalReason()
        );
    }
}
