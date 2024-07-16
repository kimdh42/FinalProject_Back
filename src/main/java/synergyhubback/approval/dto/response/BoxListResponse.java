package synergyhubback.approval.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.ApprovalBox;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoxListResponse {
    private final int abCode;
    private final String abName;
    private final int empCode;

    public static BoxListResponse from(final ApprovalBox approvalBox){
        return new BoxListResponse(
                approvalBox.getAbCode(),
                approvalBox.getAbName(),
                approvalBox.getEmpCode()
        );
    }
}
