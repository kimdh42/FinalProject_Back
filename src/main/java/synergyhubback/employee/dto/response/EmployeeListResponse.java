    package synergyhubback.employee.dto.response;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import synergyhubback.employee.domain.entity.Employee;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.stream.Collectors;


    @Getter
    @Setter
    @AllArgsConstructor
    public class EmployeeListResponse {

        private List<EmployeeResponse> employees;

        public static EmployeeListResponse getEmployeeList(List<Employee> employees) {
            List<EmployeeResponse> employeeResponses = employees.stream()
                    .map(EmployeeResponse::fromEntity)
                    .collect(Collectors.toList());
            return new EmployeeListResponse(employeeResponses);
        }
    }


