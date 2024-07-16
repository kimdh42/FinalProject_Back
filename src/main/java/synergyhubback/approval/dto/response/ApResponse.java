package synergyhubback.approval.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.Personal;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate apStart;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate apEnd;
    private final String apContact;
    private final String apReason;

    public static ApResponse from(final Personal personal){
        return new ApResponse(
                personal.getApStart(),
                personal.getApEnd(),
                personal.getApContact(),
                personal.getApReason()
        );
    }
}
