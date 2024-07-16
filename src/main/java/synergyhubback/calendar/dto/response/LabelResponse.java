package synergyhubback.calendar.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.calendar.domain.entity.Label;

@Getter
@RequiredArgsConstructor
public class LabelResponse {

    private final Long labelCode;
    private final String labelTitle;
    private final String labelCon;
    private final String labelColor;

    public static LabelResponse from(final Label label) {
        return new LabelResponse(
                label.getId(),
                label.getLabelTitle(),
                label.getLabelCon(),
                label.getLabelColor()
        );
    }
}
