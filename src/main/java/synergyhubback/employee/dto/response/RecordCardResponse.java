package synergyhubback.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Certificate;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.entity.SchoolInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
public class RecordCardResponse {

    private int emp_code;
    private String emp_name;
    private String social_security_no;
    private String email;
    private String phone;
    private String address;
    private String emp_img;
    private List<SchoolInfo> schoolInfos;

    private List<Certificate> certificates;

    public static RecordCardResponse getRecordCard(Employee employee, List<SchoolInfo> schoolInfos, List<Certificate> certificates) {


        return new RecordCardResponse(
                employee.getEmp_code(),
                employee.getEmp_name(),
                employee.getSocial_security_no(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAddress(),
                employee.getEmp_img(),
                schoolInfos,
                certificates
        );
    }
}
