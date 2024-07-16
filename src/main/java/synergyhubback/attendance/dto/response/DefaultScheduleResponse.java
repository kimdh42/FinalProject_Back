package synergyhubback.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.attendance.domain.entity.DefaultSchedule;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.DeptRelations;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class DefaultScheduleResponse {

    private int dsCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dsStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dsEndDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime atdStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime atdEndTime;

    private String parTitle;
    private String subTitle;
    private String deptTitle;
    private String empName;

    public DefaultScheduleResponse(DefaultSchedule defaultSchedule) {
        this.dsCode = defaultSchedule.getDsCode();
        this.dsStartDate = defaultSchedule.getDsStartDate();
        this.dsEndDate = defaultSchedule.getDsEndDate();
        this.atdStartTime = defaultSchedule.getAtdStartTime();
        this.atdEndTime = defaultSchedule.getAtdEndTime();
        this.parTitle = defaultSchedule.getParTitle();
        this.subTitle = defaultSchedule.getSubTitle();
        this.deptTitle = defaultSchedule.getDeptTitle();

        Optional<Employee> optionalEmployee = Optional.ofNullable(defaultSchedule.getEmployee());
        this.empName = optionalEmployee.map(emp -> emp.getEmp_name() != null ? emp.getEmp_name() : "").orElse("");
    }



}
