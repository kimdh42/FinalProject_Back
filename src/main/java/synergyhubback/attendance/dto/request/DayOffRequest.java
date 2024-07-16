package synergyhubback.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.attendance.domain.entity.DayOffBalance;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class DayOffRequest {

    private int doCode;              //휴가코드(pk)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate doReportDate;     //신청일자

    private String doName;              //휴가명

    private Double doUsed;              //신청일수

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate doStartDate;      //시작일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate doEndDate;        //종료일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime doStartTime;      //시작시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime doEndTime;        //종료시간

    private Double granted;
    private Double dbUsed;
    private Double remaining;
    private Employee employee;

    @JsonCreator
    public DayOffRequest(@JsonProperty("doName") String doName,
                         @JsonProperty("doReportDate") LocalDate doReportDate,
                         @JsonProperty("doUsed") Double doUsed,
                         @JsonProperty("doStartDate") LocalDate doStartDate,
                         @JsonProperty("doEndDate") LocalDate doEndDate,
                         @JsonProperty("doStartTime") LocalTime doStartTime,
                         @JsonProperty("doEndTime") LocalTime doEndTime,
                         @JsonProperty("granted") Double granted,
                         @JsonProperty("dbUsed") Double dbUsed,
                         @JsonProperty("remaining") Double remaining,
                         @JsonProperty("employee") Employee employee) {
        this.doName = doName;
        this.doReportDate = doReportDate;
        this.doUsed = doUsed;
        this.doStartDate = doStartDate;
        this.doEndDate = doEndDate;
        this.doStartTime = doStartTime;
        this.doEndTime = doEndTime;
        this.granted = granted;
        this.dbUsed = dbUsed;
        this.remaining = remaining;
        this.employee = employee;
    }

}
