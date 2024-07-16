package synergyhubback.employee.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Bank;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeUpdateRequest {

        private int emp_code;           // 사원코드
        private String emp_name;        // 사원이름
        private String emp_img;
        private String emp_pass;        // 비밀번호
        private String new_emp_pass;    // 새 비밀번호
        private String email;           // 이메일
        private String phone;           // 휴대폰
        private String address;         // 주소
        private String bank_name;       // 은행이름
        private String account_num;        // 계좌번호

        public EmployeeUpdateRequest() {}
}

