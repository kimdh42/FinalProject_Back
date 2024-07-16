package synergyhubback.attendance.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synergyhubback.approval.dto.response.DocListResponse;
import synergyhubback.approval.dto.response.DocumentResponse;
import synergyhubback.attendance.domain.entity.DayOff;
import synergyhubback.attendance.domain.entity.DefaultSchedule;
import synergyhubback.attendance.dto.request.*;
import synergyhubback.attendance.dto.response.*;
import synergyhubback.attendance.service.AttendanceService;
import synergyhubback.auth.util.TokenUtils;
import synergyhubback.calendar.dto.response.TaskResponse;
import synergyhubback.calendar.service.TaskService;
import synergyhubback.employee.dto.response.EmployeeListResponse;
import synergyhubback.employee.service.EmployeeService;
import synergyhubback.message.dto.response.ReceiveResponse;
import synergyhubback.message.service.MessageService;
import synergyhubback.post.domain.entity.PostEntity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final TaskService taskService;
    private final MessageService messageService;

    // 날짜 포맷을 상수로 정의
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /* ------------------------------------ 근태일지 생성 ------------------------------------  */

    //출근시간 등록기능
    @Operation(summary = "출근 시간 등록", description = "출근 시간을 등록한다.")
    @PostMapping("/registStartTime")
    public ResponseEntity<ResponseMessage> registAttendanceStartTime(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            AttendanceRegistStartTimeRequest updateAttendance = attendanceService.registAttendanceStartTime(empCode);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("attendance", updateAttendance);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage(200, "출근 시간 등록 성공", responseMap));

        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage(404, "근태 기록을 찾을 수 없습니다.", null));
        }
    }

    //퇴근시간 등록기능
    @Operation(summary = "퇴근 시간 등록", description = "퇴근 시간을 등록한다.")
    @PostMapping("/registEndTime")
    public ResponseEntity<ResponseMessage> registAttendanceEndTime(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            AttendanceRegistEndTimeRequest updateAttendance = attendanceService.registAttendanceEndTime(empCode);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("attendance", updateAttendance);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage(200, "퇴근 시간 등록 성공", responseMap));

        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage(404, "근태 기록을 찾을 수 없습니다.", null));
        }
    }

    /* ------------------------------------ 지정 출퇴근시간 ------------------------------------ */

    // 지정 출퇴근시간 등록
    @Operation(summary = "지정 출퇴근시간 등록", description = "지정 출퇴근시간을 등록한다.")
    @PostMapping(value = "/registSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> registDefaultSchedule(@RequestBody DefaultScheduleRequest request) {
        try {
            // Null 체크 추가
            if (request.getParTitle() == null && request.getSubTitle() == null && request.getDeptTitle() == null) {
                throw new IllegalArgumentException("부서나 팀 중 하나는 반드시 선택해야합니다.");
            } else if (request.getAtdStartTime() == null) {
                throw new IllegalArgumentException("지정 출근시간을 설정해야합니다.");
            } else if (request.getAtdEndTime() == null) {
                throw new IllegalArgumentException("지정 퇴근시간을 설정해야합니다.");
            }

            // 지정 출퇴근시간 등록
            attendanceService.registDefaultSchedule(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage(200, "지정 출퇴근시간 등록 성공", null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(500, "서버 오류: " + e.getMessage(), null));
        }
    }

    // 지정 출퇴근시간 조회 (단체)
    @Operation(summary = "전체 근태 기록 조회", description = "전체 근태 목록을 조회한다.")
    @GetMapping("/allSchedules")
    public ResponseEntity<ResponseMessage> findAllSchedules() {

        List<DefaultScheduleResponse> schedules = attendanceService.findAllDefaultSchedules();
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("schedules", schedules);
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 지정 출퇴근시간 수정
    @Operation(summary = "지정 출퇴근시간 수정", description = "지정 출퇴근시간을 수정한다.")
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping(value = "/updateSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> updateDefaultSchedule(@RequestBody DefaultScheduleRequest request) {
        try {
            // Null 체크 추가
            if (request.getDeptTitle() == null) {
                throw new IllegalArgumentException("DeptTitle이 null입니다.");
            } else if (request.getAtdStartTime() == null) {
                throw new IllegalArgumentException("StartTime이 null입니다.");
            } else if (request.getAtdEndTime() == null) {
                throw new IllegalArgumentException("endTime이 null입니다.");
            }

            // 출퇴근시간 수정
            attendanceService.updateDefaultSchedule(request.getDeptTitle(), request.getAtdStartTime(), request.getAtdEndTime(), request.getEmployee());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage(200, "지정 출퇴근시간 수정 성공", null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(500, "서버 오류: " + e.getMessage(), null));
        }
    }

    // 지정 출퇴근시간 삭제
    @Operation(summary = "지정 출퇴근시간 삭제", description = "지정 출퇴근시간을 삭제한다.")
    @DeleteMapping("/DeleteSchedule/{dsCode}")
    public ResponseEntity<ResponseMessage> deleteSchedule(@PathVariable int dsCode) {
        boolean deleted = attendanceService.deleteSchedule(dsCode);
        if(deleted) {
            return ResponseEntity.ok().body(new ResponseMessage(200, "삭제 성공", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(404, "삭제할 데이터가 없습니다.", null));
        }
    }


    /* ------------------------------------ 근태 기록 ------------------------------------ */

    // 개인별
    @Operation(summary = "이번 주의 근태 기록 조회", description = "이번 주의 근태 목록을 조회한다.")
    @GetMapping("/my-current-week")
    public ResponseEntity<ResponseMessage> getMyAttendancesForCurrentWeek(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<AttendancesResponse> attendancesForCurrentWeek = attendanceService.getMyAttendancesForCurrentWeek(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("attendances", attendancesForCurrentWeek);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    // 개인
    @Operation(summary = "전체 근태 기록 조회", description = "전체 근태 목록을 조회한다.")
    @GetMapping("/my-all")
    public ResponseEntity<ResponseMessage> findAllAttendances(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<AttendancesResponse> attendances = attendanceService.findAllMyAttendances(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("attendances", attendances); // 복수형으로 변경: attendance -> attendances
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }


    // 전체
    @Operation(summary = "전체 근태 기록 조회", description = "전체 근태 목록을 조회한다.")
    @GetMapping("/all")
    public ResponseEntity<ResponseMessage> findAllAttendances() {
        List<AttendancesResponse> attendances = attendanceService.findAllAttendances();
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("attendances", attendances); // 복수형으로 변경: attendance -> attendances
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    @Operation(summary = "이번 주의 근태 기록 조회", description = "이번 주의 근태 목록을 조회한다.")
    @GetMapping("/current-week")
    public ResponseEntity<ResponseMessage> getAttendancesForCurrentWeek() {
        List<AttendancesResponse> attendancesForCurrentWeek = attendanceService.getAttendancesForCurrentWeek();
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("attendances", attendancesForCurrentWeek);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    @Operation(summary = "오늘의 전체 근태 기록 조회", description = "오늘의 전체 근태 기록을 조회한다.")
    @GetMapping("/today-all")
    public ResponseEntity<ResponseMessage> getAttendanceForTodayAll(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

        List<AttendancesResponse> attendancesForToday = attendanceService.getAllAttendancesForToday();
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("AllAttendanceToday", attendancesForToday); // 단수형으로 변경: attendances -> attendance

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "조회 성공", responseMap));
    }


    @Operation(summary = "오늘의 개인 근태 기록 조회", description = "오늘의 개인 근태 기록을 조회한다.")
    @GetMapping("/today")
    public ResponseEntity<ResponseMessage> getAttendanceForToday(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        AttendancesResponse attendancesForToday = attendanceService.getAttendancesForToday(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("attendance", attendancesForToday); // 단수형으로 변경: attendances -> attendance

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    /* ------------------------------------ 초과근무 ------------------------------------ */

    @Operation(summary = "초과근무 기록 조회", description = "초과근무 기록을 조회한다.")
    @GetMapping("/overwork")
    public ResponseEntity<ResponseMessage> findAllOverTimeWork() {
        List<OverWorkResponse> overWorks = attendanceService.findAllOverTimeWork();
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("overWorks", overWorks);
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    /* ------------------------------------ 휴가 ------------------------------------ */

    // 휴가 전체 조회
    @Operation(summary = "휴가 기록 조회", description = "휴가 기록을 조회한다.")
    @GetMapping("/dayOff")
    public ResponseEntity<ResponseMessage> findAllDayOff() {

        List<DayOffResponse> dayOffs = attendanceService.findAllDayOff();
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("dayOffs", dayOffs);
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 휴가 개인 기록 조회
    @Operation(summary = "휴가 기록 조회", description = "휴가 기록을 조회한다.")
    @GetMapping("/my-dayOff")
    public ResponseEntity<ResponseMessage> findAllDayOff(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<DayOffResponse> dayOffs = attendanceService.findAllDayOffByEmpCode(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("dayOffs", dayOffs);
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 개인 휴가 보유 내역 조회
    @Operation(summary = "개인 휴가 보유 내역 조회", description = "개인의 휴가 보유 내역을 조회한다.")
    @GetMapping("/my-dayOffBalance")
    public ResponseEntity<ResponseMessage> findAllDayOffBalance(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        DayOffBalanceResponse dayOffBalance = attendanceService.findAllDayOffBalanceByEmpCode(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("dayOffBalance", dayOffBalance);
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 휴가 등록
    @Operation(summary = "휴가 등록", description = "휴가를 등록한다.")
    @PostMapping(value = "/registDayOff", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> registDayOff(@RequestBody DayOffRequest request) {
        try {
            // Null 체크 추가
            if (request.getDoStartDate() == null) {
                throw new IllegalArgumentException("시작일자를 입력하지 않았습니다.");
            } else if (request.getDoEndDate() == null) {
                throw new IllegalArgumentException("종료일자를 입력하지 않았습니다..");
            } else if (request.getDoStartTime() == null) {
                throw new IllegalArgumentException("시작시간을 입력하지 않았습니다.");
            } else if (request.getDoEndTime() == null) {
                throw new IllegalArgumentException("종료시간을 입력하지 않았습니다.");
            } else if (request.getDoName() == null) {
                throw new IllegalArgumentException("휴가 종류를 입력하지 않았습니다.");
            }

            // 휴가 등록
            attendanceService.registDayOff(
                    request.getDoReportDate(),
                    request.getDoName(), request.getDoUsed(),
                    request.getDoStartDate(), request.getDoEndDate(),
                    request.getDoStartTime(), request.getDoEndTime(),
                    request.getEmployee());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage(200, "휴가 등록 성공", null));

        } catch(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(500, "서버 오류: " + e.getMessage(), null));
        }
    }

    // 연차 촉진 이메일 발송 서비스
    @Operation(summary = "연차 촉진 이메일 발송", description = "연차 촉진 이메일을 발송한다.")
    @PostMapping("/sendEmail")
    public ResponseEntity<ResponseMessage> sendDayOffEmail(@RequestParam List<Integer> empCodes) throws MessagingException {
        // 이메일 발송
        for (Integer empCode : empCodes) {
            attendanceService.sendDayOffEmail(empCode);
        }

        HttpHeaders headers = new HttpHeaders();
        ResponseMessage responseMessage = new ResponseMessage(200, "이메일 발송 성공", null);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 예외근무 현황 조회
    @GetMapping("/currentBT")
    public ResponseEntity<ResponseMessage> findBTDocList(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<DocumentResponse> currentBT = attendanceService.findBTDocList(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("document", currentBT);
        ResponseMessage responseMessage = new ResponseMessage(200, "예외근무 현황 조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 초과근무 현황 조회
    @GetMapping("/currentOW")
    public ResponseEntity<ResponseMessage> findOWDocList(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<DocumentResponse> currentOW = attendanceService.findOWDocList(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("document", currentOW);
        ResponseMessage responseMessage = new ResponseMessage(200, "초과근무 현황 조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 휴가신청 현황 조회
    @GetMapping("/currentDO")
    public ResponseEntity<ResponseMessage> findDODocList(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<DocumentResponse> currentDO = attendanceService.findDODocList(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("document", currentDO);
        ResponseMessage responseMessage = new ResponseMessage(200, "휴가신청 현황 조회 성공", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 월간 휴가신청 승인현황 조회
    @GetMapping("/monthDO")
    public ResponseEntity<ResponseMessage> findDODoneDocList(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<DocumentResponse> monthDO = attendanceService.findDODoneDocList(empCode);
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("document", monthDO);
        ResponseMessage responseMessage = new ResponseMessage(200, "월간 휴가 승인 현황", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 메인페이지를 위한 부재자 목록
    @GetMapping("/absentee")
    public ResponseEntity<ResponseMessage> findAbsentee() {

        List<AttendancesResponse> absentee = attendanceService.findAbsentee();

        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("absentee", absentee);
        ResponseMessage responseMessage = new ResponseMessage(200, "부재자 현황", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 메인페이지를 위한 이번 달 생일자 목록
    @GetMapping("/birth")
    public ResponseEntity<EmployeeListResponse> getAllInfo() {

        EmployeeListResponse allInfo = attendanceService.findBirth();

        return ResponseEntity.ok(allInfo);
    }

    // 메인페이지를 위한 공지사항 목록
    @GetMapping("/notice")
    public ResponseEntity<ResponseMessage> findNotice() {

        List<PostEntity> posts = attendanceService.InboardList(1);

        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("posts", posts);
        ResponseMessage responseMessage = new ResponseMessage(200, "공지사항 조회 완료", responseMap);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseMessage);
    }

    // 메인페이지를 위한 읽지 않은 쪽지 목록 : MSG_STATUS = N
    @GetMapping("/message-n")
    public ResponseEntity<List<ReceiveResponse>> getReceiveMessage(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<ReceiveResponse> receiveList = attendanceService.getReceiveMessage(empCode);

        System.out.println("receiveList : controller : " + receiveList.size());

        return new ResponseEntity<>(receiveList, HttpStatus.OK);
    }

    // 메인페이지를 위한 진행 중인 업무 목록 :
    @GetMapping("/tasks-b")
    public ResponseEntity<List<TaskResponse>> getAllTasks(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            List<TaskResponse> tasks = taskService.findAllTasksByEmpCode(empCode).stream().map(TaskResponse::from).collect(Collectors.toList());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
