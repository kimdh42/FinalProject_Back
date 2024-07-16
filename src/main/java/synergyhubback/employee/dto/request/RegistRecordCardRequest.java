package synergyhubback.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistRecordCardRequest {

    private int emp_code;
    private String emp_name;
    private String social_security_no;
    private String phone;
    private String email;
    private String address;
    private List<RegistSchoolInfoRequest> schoolInfos;
    private List<RegistCertificateRequest> certificates;

}
