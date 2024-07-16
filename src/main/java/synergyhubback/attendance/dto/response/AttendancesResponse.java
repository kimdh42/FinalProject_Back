package synergyhubback.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.attendance.domain.entity.Attendance;
import synergyhubback.attendance.domain.entity.AttendanceStatus;
import synergyhubback.attendance.domain.entity.OverWork;
import synergyhubback.employee.domain.entity.DeptRelations;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Getter
@Setter
public class AttendancesResponse {


    private int atdCode;                                                  //근태코드(pk)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate atdDate;                                            //근무날짜

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime atdStartTime;                                       //지정출근시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime atdEndTime;                                         //지정퇴근시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;                                          //출근시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;                                            //퇴근시간

    private String parTitle;  //상위부서명
    private String subTitle;  //하위부서명
    private String deptTitle; //부서명
    private String empName;   //사원명
    private String empTitle;
    private String empBirth;
    private String empImg;


    private AttendanceStatus attendanceStatus;                            //근무상태코드
    private int empCode;                                                    //사원코드

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime owStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime owEndTime;

    public AttendancesResponse(Attendance attendance) {
        this.empCode = attendance.getEmployee().getEmp_code();
        this.empTitle = attendance.getEmployee().getTitle().getTitle_name();
        this.empName = attendance.getEmployee().getEmp_name();
        this.atdDate = attendance.getAtdDate();
        this.atdStartTime = attendance.getAtdStartTime();
        this.atdEndTime = attendance.getAtdEndTime();
        this.startTime = attendance.getStartTime();
        this.endTime = attendance.getEndTime();
        this.attendanceStatus = attendance.getAttendanceStatus();
        this.empImg = attendance.getEmployee().getEmp_img();
        if (attendance.getOverWork() != null) {
            this.owStartTime = attendance.getOverWork().getOwStartTime();
            this.owEndTime = attendance.getOverWork().getOwEndTime();
        }

        // empBirth 처리
        if (attendance.getEmployee().getSocial_security_no() != null) {
            String birth = attendance.getEmployee().getSocial_security_no();

            // 주민등록번호에서 생년월일 부분 추출 (YYMMDD)
            String birthDate = birth.substring(0, 6);

            // 월일 부분 추출 (MMDD 형식)
            String monthDay = birthDate.substring(2, 6);

            String formattedMonthDay = formatMonthDay(monthDay);

            this.empBirth = formattedMonthDay;
        } else {
            this.empBirth = null; // null 처리
        }


        // 상위부서명 가져오기
        if (attendance.getEmployee().getTitle().getTitle_code().equals("T1") || attendance.getEmployee().getTitle().getTitle_code().equals("T2")) {

            // 팀명
            this.deptTitle = null;

            // 하위부서
            this.subTitle = null; // 하위부서는 null로 설정

            // 상위부서
            this.parTitle = attendance.getEmployee().getDepartment().getDept_title(); // 현재 부서를 상위부서로 설정

        }  else if (attendance.getEmployee().getTitle().getTitle_code().equals("T4")) {

            // 팀명
            this.deptTitle = null; // 팀명은 null로 설정

            // 하위부서 (내 부서)
            this.subTitle = attendance.getEmployee().getDepartment().getDept_title();

            StringBuilder parentDeptTitles = new StringBuilder();
            Set<DeptRelations> parentDepartments = attendance.getEmployee().getDepartment().getParentDepartments();
            for (DeptRelations deptRelation : parentDepartments) {
                parentDeptTitles.append(deptRelation.getParentDepartment().getDept_title()).append(", ");
            }

            if (parentDeptTitles.length() > 2) {
                parentDeptTitles.setLength(parentDeptTitles.length() - 2); // 마지막 쉼표와 공백 제거
            } else if (parentDeptTitles.length() == 1) {
                parentDeptTitles.setLength(parentDeptTitles.length());
            }

            // 상위부서
            this.parTitle = parentDeptTitles.toString();

        } else if (attendance.getEmployee().getTitle().getTitle_code().equals("T5") || attendance.getEmployee().getTitle().getTitle_code().equals("T6")) {

            // 팀명
            this.deptTitle = attendance.getEmployee().getDepartment().getDept_title();

            StringBuilder parentDeptTitles = new StringBuilder();
            Set<DeptRelations> parentDepartments = attendance.getEmployee().getDepartment().getParentDepartments();
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

    // 월일 형식을 변환하는 메서드
    private static String formatMonthDay(String monthDay) {
        if (monthDay.length() != 4) {
            throw new IllegalArgumentException("월일 형식이 올바르지 않습니다.");
        }

        // 월과 일을 추출
        String month = monthDay.substring(0, 2);
        String day = monthDay.substring(2, 4);

        // 정수로 변환하여 0으로 시작하는 경우 앞에 0을 제거
        int monthInt = Integer.parseInt(month);
        int dayInt = Integer.parseInt(day);

        // 문자열로 다시 변환하면서 월과 일을 포맷
        return String.format("%d월 %d일", monthInt, dayInt);
    }

}


