package synergyhubback.calendar.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LabelCreateRequest {

    @NotBlank
    private final String labelTitle;

    private final String labelCon;

    private final String labelColor;
}

