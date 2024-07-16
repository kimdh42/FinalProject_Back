package synergyhubback.approval.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.Etc;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AeResponse {
    private final String aeCon;

    public static AeResponse from(final Etc etc){
        return new AeResponse(etc.getAeCon());
    }
}
