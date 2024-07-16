package synergyhubback.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.DetailByEmpRegist;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class EmpRegistDetailListResponse {

    private String erd_num;

    private String erd_title;

    private List<EmpRegistDetailResponse> empRegistDetailResponseList;

    public static EmpRegistDetailListResponse fromEntity(String erd_num, String erd_title, List<DetailByEmpRegist> details) {

        List<EmpRegistDetailResponse> empRegistDetailResponseList = details.stream()
                .map(EmpRegistDetailResponse::fromEntity)
                .collect(Collectors.toList());

        return new EmpRegistDetailListResponse(erd_num, erd_title, empRegistDetailResponseList);
    }

}
