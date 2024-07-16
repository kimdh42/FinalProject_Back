package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Title;

@Getter
@Setter
@AllArgsConstructor
public class EmpTitleListResponse {
    private String title_code;
    private String title_name;

    public static EmpTitleListResponse from(Title title) {
        return new EmpTitleListResponse(
                title.getTitle_code(),
                title.getTitle_name()
        );
    }
}
