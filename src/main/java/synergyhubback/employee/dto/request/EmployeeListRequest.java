    package synergyhubback.employee.dto.request;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.Setter;
    import synergyhubback.employee.domain.entity.Employee;
    import synergyhubback.employee.dto.response.EmployeeResponse;

    import java.util.List;
    import java.util.stream.Collectors;


    @Getter
    @Setter
    @AllArgsConstructor
    public class EmployeeListRequest {

        private List<EmployeeResponse> employees;

        public static EmployeeListRequest getEmployeeList(List<Employee> employees) {

            List<EmployeeResponse> employeeResponses = employees.stream()
                    .map(employee -> new EmployeeResponse(

                            employee.getEmp_code(),
                            employee.getPar_code(),
                            employee.getEmp_name(),
                            employee.getDepartment().getDept_code(),
                            employee.getDepartment().getDept_title(),
                            employee.getPosition().getPosition_code(),
                            employee.getPosition().getPosition_name(),
                            employee.getTitle().getTitle_name(),
                            employee.getPhone(),
                            employee.getHire_date(),
                            employee.getEmp_status(),
                            employee.getSocial_security_no(),
                            employee.getEmp_img()

                    ))
                    .collect(Collectors.toList());
            return new EmployeeListRequest(employeeResponses);
        }
    }


