package synergyhubback.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {

        private int emp_code;
        private Integer par_code;
        private String emp_name;
        private String dept_code;
        private String dept_title;
        private String position_code;
        private String position_name;
        private String title_name;
        private String email;
        private String phone;
        private String address;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate hire_date;
        private String emp_status;
        private String social_security_no;              //생년월일로 포맷함
        private String emp_img;
        private String bank_name;
        private String account_num;
        private int direct_line;

        private String par_dept_code;   // 부서의 상위 부서 코드
        private String sub_dept_code;   // 부서의 하위 부서 코드

        public EmployeeResponse(int empCode, Integer parCode, String empName, String deptCode, String deptTitle, String positionCode, String positionName, String titleName, String phone, LocalDate hireDate, String empStatus, String socialSecurityNo, String empImg) {

        }


        public static EmployeeResponse fromEntity(Employee employee) {
                EmployeeResponse response = new EmployeeResponse();
                response.setEmp_code(employee.getEmp_code());
                response.setPar_code(employee.getPar_code());
                response.setEmp_name(employee.getEmp_name());
                response.setDept_code(employee.getDepartment().getDept_code());
                response.setDept_title(employee.getDepartment().getDept_title());
                response.setPosition_code(employee.getPosition().getPosition_code());
                response.setPosition_name(employee.getPosition().getPosition_name());
                response.setTitle_name(employee.getTitle().getTitle_name());
                response.setEmail(employee.getEmail());
                response.setPhone(employee.getPhone());
                response.setAddress(employee.getAddress());
                response.setHire_date(employee.getHire_date());
                response.setEmp_status(employee.getEmp_status());
                response.setSocial_security_no(employee.getSocial_security_no());
                response.setEmp_img(employee.getEmp_img());
                response.setBank_name(employee.getBank().getBank_name());
                response.setAccount_num(employee.getAccount_num());
                response.setDirect_line(employee.getDirect_line());

                // 부서의 상위 부서 코드 설정
                response.setPar_dept_code(employee.getDepartment().getParentDepartments().isEmpty() ?
                        null : employee.getDepartment().getParentDepartments().iterator().next().getParentDepartment().getDept_code());

                // 부서의 하위 부서 코드 설정
                response.setSub_dept_code(employee.getDepartment().getSubDepartments().isEmpty() ?
                        null : employee.getDepartment().getSubDepartments().iterator().next().getSubDepartment().getDept_code());

                return response;
        }

        public String formatSocialSecurityNo() {
                if (social_security_no != null) {
                        String cleanedSSN = social_security_no.replaceAll("[^0-9]", "");

                        if (cleanedSSN.length() >= 6) {
                                String datePart = cleanedSSN.substring(0, 6);

                                try {
                                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
                                        LocalDate date = LocalDate.parse(datePart, inputFormatter);

                                        int yearPart = Integer.parseInt(cleanedSSN.substring(0, 2));
                                        int century = determineCentury(yearPart);

                                        date = date.withYear(century + yearPart);

                                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        return date.format(outputFormatter);

                                } catch (DateTimeParseException | NumberFormatException e) {
                                        e.printStackTrace();
                                }
                        }
                }
                return social_security_no;
        }
        private int determineCentury(int yearPart) {
                if (yearPart >= 00 && yearPart <= 24) {
                        return 2000;
                } else {
                        return 1900;
                }
        }

        @JsonGetter("social_security_no")
        public String getFormattedSocialSecurityNo() {
                return formatSocialSecurityNo();
        }

        public void setDept_title(String dept_title) {
                this.dept_title = dept_title;
        }

        public void setPosition_name(String position_name) {
                this.position_name = position_name;
        }

}

