package synergyhubback.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.*;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class DocRegistRequest {
    private String adCode;
    @NotBlank
    private String adTitle;

    private Employee employee;

    @NotNull
    private LocalDate adReportDate;
    @NotBlank
    private String adStatus;

    private String adDetail;

    private Form form;
    private Etc etc;
    private Personal personal;
    private ApprovalAttendance approvalAttendance;
    private ApprovalAppoint approvalAppoint;
    private List<AppointDetail> appointDetailList;

    private List<TrueLine> trueLineList;
}
