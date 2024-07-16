package synergyhubback.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class OverWorkRequest {

    private int owCode;             // 초과근무코드(pk)
    private LocalDate owDate;       // 초과근무 일자
    private LocalTime owStartTime;  // 시작시간
    private LocalTime owEndTime;    // 종료시간
    private Employee employee;      // 사원코드 (추후 fk)

    @JsonCreator
    public OverWorkRequest(@JsonProperty("owCode") int owCode,
                            @JsonProperty("owDate") LocalDate owDate,
                            @JsonProperty("owStartTime") LocalTime owStartTime,
                            @JsonProperty("owEndTime") LocalTime owEndTime,
                            @JsonProperty("employee") Employee employee) {
        this.owCode = owCode;
        this.owDate = owDate;
        this.owStartTime = owStartTime;
        this.owEndTime = owEndTime;
        this.employee = employee;
    }
}
