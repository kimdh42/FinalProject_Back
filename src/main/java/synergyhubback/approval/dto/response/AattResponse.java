package synergyhubback.approval.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.ApprovalAttendance;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AattResponse {
    private final String aattSort;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime aattStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime aattEnd;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate aattOccur;
    private final String aattPlace;
    private final String aattCon;
    private final String aattReason;

    public static AattResponse from(final ApprovalAttendance approvalAttendance){
        return new AattResponse(
                approvalAttendance.getAattSort(),
                approvalAttendance.getAattStart(),
                approvalAttendance.getAattEnd(),
                approvalAttendance.getAattOccur(),
                approvalAttendance.getAattPlace(),
                approvalAttendance.getAattCon(),
                approvalAttendance.getAattReason()
        );
    }
}
