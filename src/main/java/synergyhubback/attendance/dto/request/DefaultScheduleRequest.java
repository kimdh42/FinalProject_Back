package synergyhubback.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultScheduleRequest {

    private LocalDate dsStartDate;
    private LocalDate dsEndDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime atdStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime atdEndTime;

    private String parTitle;
    private String subTitle;
    private String deptTitle;
    private Employee employee;

    @JsonCreator
    @Builder
    public DefaultScheduleRequest(
            @JsonProperty("dsStartDate") LocalDate dsStartDate,
            @JsonProperty("dsEndDate") LocalDate dsEndDate,
            @JsonProperty("atdStartTime") LocalTime atdStartTime,
            @JsonProperty("atdEndTime") LocalTime atdEndTime,
            @JsonProperty("parTitle") String parTitle,
            @JsonProperty("subTitle") String subTitle,
            @JsonProperty("deptTitle") String deptTitle,
            @JsonProperty("employee") Employee employee) {
        this.dsStartDate = dsStartDate;
        this.dsEndDate = dsEndDate;
        this.atdStartTime = atdStartTime;
        this.atdEndTime = atdEndTime;
        this.parTitle = parTitle;
        this.subTitle = subTitle;
        this.deptTitle = deptTitle;
        this.employee = employee;
    }

}
