package synergyhubback.approval.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.TrueLine;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewLineResponse {
    private final int talOrder;
    private final String talRole;
    private final String talStatus;
    @JsonFormat(pattern = "yyyy.MM.dd")
    private final LocalDate talDate;
    private final String deptTitle;
    private final String titleName;
    private final int empCode;
    private final String empName;
    private final Integer empSign;
    private final String talReason;

    public static ViewLineResponse from(final TrueLine trueLine){
        return new ViewLineResponse(
                trueLine.getTalOrder(),
                trueLine.getTalRole(),
                trueLine.getTalStatus(),
                trueLine.getTalDate(),
                trueLine.getEmployee().getDepartment().getDept_title(),
                trueLine.getEmployee().getTitle().getTitle_name(),
                trueLine.getEmployee().getEmp_code(),
                trueLine.getEmployee().getEmp_name(),
                trueLine.getEmployee().getEmp_sign(),
                trueLine.getTalReason()
        );
    }
}
