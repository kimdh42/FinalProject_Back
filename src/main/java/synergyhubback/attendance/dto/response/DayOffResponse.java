package synergyhubback.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.attendance.domain.entity.DayOff;
import synergyhubback.attendance.domain.entity.DayOffBalance;
import synergyhubback.employee.domain.entity.DeptRelations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class DayOffResponse {

    private int doCode;                                                      //휴가코드(pk)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate doReportDate;                                          //신청일자

    private String doName;                                                   //휴가명

    private Double doUsed;                                                      //신청일수

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate doStartDate;                                           //시작일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate doEndDate;                                             //종료일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime doStartTime;                                           //시작시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime doEndTime;                                             //종료시간

    private Double granted;                                                     //부여일수

    private Double dbUsed;                                                      //사용일수

    private Double remaining;                                                   //잔여일수

    private int empCode;      //사원코드
    private String parTitle;  //상위부서명
    private String subTitle;  //하위부서명
    private String deptTitle; //부서명
    private String empName;   //사원명


    public DayOffResponse(DayOff dayOff) {
        this.doCode = dayOff.getDoCode();
        this.doReportDate = dayOff.getDoReportDate();
        this.doName = dayOff.getDoName();
        this.doUsed = dayOff.getDoUsed();
        this.doStartDate = dayOff.getDoStartDate();
        this.doEndDate = dayOff.getDoEndDate();
        this.doStartTime = dayOff.getDoStartTime();
        this.doEndTime = dayOff.getDoEndTime();
        this.granted = dayOff.getGranted();
        this.remaining = dayOff.getRemaining();
        this.dbUsed = dayOff.getDbUsed();
        this.empCode = dayOff.getEmployee().getEmp_code();
        this.empName = dayOff.getEmployee().getEmp_name();

        // 상위부서명 가져오기
        if (dayOff.getEmployee().getTitle().getTitle_code().equals("T1") || dayOff.getEmployee().getTitle().getTitle_code().equals("T2")) {

            // 팀명
            this.deptTitle = null;

            // 하위부서
            this.subTitle = null; // 하위부서는 null로 설정

            // 상위부서
            this.parTitle = dayOff.getEmployee().getDepartment().getDept_title(); // 현재 부서를 상위부서로 설정

        }  else if (dayOff.getEmployee().getTitle().getTitle_code().equals("T4")) {

            // 팀명
            this.deptTitle = null; // 팀명은 null로 설정

            // 하위부서
            this.subTitle = null;

            StringBuilder parentDeptTitles = new StringBuilder();
            Set<DeptRelations> parentDepartments = dayOff.getEmployee().getDepartment().getParentDepartments();
            for (DeptRelations deptRelation : parentDepartments) {
                parentDeptTitles.append(deptRelation.getParentDepartment().getDept_title()).append(", ");
            }

            if (parentDeptTitles.length() > 2) {
                parentDeptTitles.setLength(parentDeptTitles.length() - 2); // 마지막 쉼표와 공백 제거
            } else if (parentDeptTitles.length() == 1) {
                parentDeptTitles.setLength(parentDeptTitles.length());
            }

            // 상위부서
            this.parTitle = dayOff.getEmployee().getDepartment().getDept_title();

        } else if (dayOff.getEmployee().getTitle().getTitle_code().equals("T5") || dayOff.getEmployee().getTitle().getTitle_code().equals("T6")) {

            // 팀명
            this.deptTitle = dayOff.getEmployee().getDepartment().getDept_title();

            StringBuilder parentDeptTitles = new StringBuilder();
            Set<DeptRelations> parentDepartments = dayOff.getEmployee().getDepartment().getParentDepartments();
            for (DeptRelations deptRelation : parentDepartments) {
                parentDeptTitles.append(deptRelation.getParentDepartment().getDept_title()).append(", ");
                // 상위부서의 상위부서명 가져오기
                for (DeptRelations grandParentRelation : deptRelation.getParentDepartment().getParentDepartments()) {

                    // 상위부서
                    this.parTitle = grandParentRelation.getParentDepartment().getDept_title();
                }
            }
            if (parentDeptTitles.length() > 2) {
                parentDeptTitles.setLength(parentDeptTitles.length() - 2); // 마지막 쉼표와 공백 제거
            } else if (parentDeptTitles.length() == 1) {
                parentDeptTitles.setLength(parentDeptTitles.length());
            }

            // 하위부서
            this.subTitle = parentDeptTitles.toString();
        }
    }

}
