package synergyhubback.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AappDetailRegistRequest {

    private int adetCode;      // 발령 결재 상세 코드

    private String aappNo;       // 발령 결재 코드

    private String adetBefore;     // 발령 전

    private String adetAfter;      // 발령 후

    private String adetType;       // 발령 종류

    private int empCode;        // 사원 코드

    public AappDetailRegistRequest() {

    }
}