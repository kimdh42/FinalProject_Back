package synergyhubback.approval.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.Form;

import java.sql.Clob;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FormContentResponse {
    private final String afName;
    private final String afExplain;
    private final int lsCode;
    private final String afCon;

    public static FormContentResponse from(Form form){
        return new FormContentResponse(
                form.getAfName(),
                form.getAfExplain(),
                form.getLineSort().getLsCode(),
                form.getAfCon()
        );
    }
}
