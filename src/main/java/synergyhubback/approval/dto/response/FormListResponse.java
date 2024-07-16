package synergyhubback.approval.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.Form;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FormListResponse {
    private final int afCode;
    private final String afName;
    private final String afExplain;
    private final int lsCode;
    private final char afActive;

    public static FormListResponse from(Form form){
        return new FormListResponse(
                form.getAfCode(),
                form.getAfName(),
                form.getAfExplain(),
                form.getLineSort().getLsCode(),
                form.getAfActive()
        );
    }
}
