package synergyhubback.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BoxRequest {
    @NotBlank
    private String abName;
    private int empCode;
}
