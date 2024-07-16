package synergyhubback.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.LineSort;

@RequiredArgsConstructor
@Getter
public class FormRegistRequest {
    @NotBlank
    private String afName;
    @NotBlank
    private String afExplain;
    @NotBlank
    private LineSort lineSort;
    @NotBlank
    private String afCon;
}
