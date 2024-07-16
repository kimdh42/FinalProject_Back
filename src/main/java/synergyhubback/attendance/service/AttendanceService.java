package synergyhubback.attendance.service;


import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.approval.domain.entity.ApprovalAttendance;
import synergyhubback.approval.domain.entity.Document;
import synergyhubback.approval.domain.entity.Personal;
import synergyhubback.approval.domain.repository.ApprovalAttendanceRepository;
import synergyhubback.approval.domain.repository.DocRepository;
import synergyhubback.approval.domain.repository.PersonalRepository;
import synergyhubback.approval.dto.response.DocListResponse;
import synergyhubback.approval.dto.response.DocumentResponse;
import synergyhubback.attendance.domain.entity.*;
import synergyhubback.attendance.domain.repository.*;
import synergyhubback.attendance.dto.request.*;
import synergyhubback.attendance.dto.response.*;
import synergyhubback.common.address.service.EmailService;
import synergyhubback.common.event.ApprovalCompletedEvent;
import synergyhubback.common.util.DateUtils;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.DeptRelations;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.repository.DepartmentRepository;
import synergyhubback.employee.domain.repository.DeptRelationsRepository;
import synergyhubback.employee.domain.repository.EmployeeRepository;
import synergyhubback.employee.dto.response.EmployeeListResponse;
import synergyhubback.message.domain.entity.Message;
import synergyhubback.message.domain.repository.MessageRepository;
import synergyhubback.message.dto.response.ReceiveResponse;
import synergyhubback.message.service.MessageService;
import synergyhubback.post.domain.entity.PostEntity;
import synergyhubback.post.service.PostService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static synergyhubback.employee.dto.response.EmployeeListResponse.getEmployeeList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final ModelMapper modelMapper;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final DefaultScheduleRepository defaultScheduleRepository;
    private final OverWorkRepository overWorkRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;
    private final DayOffRepository dayOffRepository;
    private final DayOffBalanceRepository dayOffBalanceRepository;
    private final DeptRelationsRepository deptRelationsRepository;
    private final DepartmentRepository departmentRepository;
    private final DocRepository docRepository;
    private final PersonalRepository personalRepository;
    private final ApprovalAttendanceRepository approvalAttendanceRepository;
    private final EmailService emailService;
    private final PostService postService;
    private final MessageRepository messageRepository;

    /* 이벤트 리스너 */
    @EventListener
    @Transactional
    public void handleApprovalCompletedEvent(ApprovalCompletedEvent event) {

        System.out.println("이벤트 리스너 수신");

        String adCode = event.getAdCode();

        Document AATTdocument = docRepository.getReferenceById(adCode);
        String AATTCode = AATTdocument.getAdDetail();
        ApprovalAttendance approvalAttendance = approvalAttendanceRepository.findByAATTCode(AATTCode);

        // 종류
        String AATTSort = approvalAttendance.getAattSort();

        if(AATTSort.equals("외근") || AATTSort.equals("출장") || AATTSort.equals("교육") || AATTSort.equals("훈련") || AATTSort.equals("재택")) {
            createAttendanceRecord(AATTdocument);
        } else if(AATTSort.equals("연장근무") || AATTSort.equals("휴일근무")) {
            if(AATTSort.equals("연장근무")) {
                registOverWork(AATTdocument);
            } else {
                createAttendanceRecord(AATTdocument); // 휴일근무면 근무일지 생성
            }
        }
    }

    /* 예외 근무, 휴가 신청 시 비동기 생성을 위한 근태일지 생성 */
    @Transactional
    public void createAttendanceRecord(Document AATTdocument) {

        System.out.println("비동기 근태 일지 생성");

        // 사원 정보
        Employee employee = AATTdocument.getEmployee();

        // 사원의 최신 근무기록 조회
        Attendance latestAttendance = attendanceRepository.findTopByOrderByAtdCodeDesc();

        // 새로운 근무기록 생성을 위한 DTO 객체 생성
        AttendanceRegistRequest newAttendanceRequest = new AttendanceRegistRequest();

        // 근무일지 코드 생성
        int atdCode = 1; // 기본값으로 설정

        if (latestAttendance != null) {
            atdCode = latestAttendance.getAtdCode() + 1;
        }
        newAttendanceRequest.setAtdCode(atdCode);


        /* ------------------------------------ 사원 코드 설정 ------------------------------------ */

        newAttendanceRequest.setEmployee(employee);

        /* ------------------------------------ 근무 시간 설정 ------------------------------------ */

        // 신청한 날짜 설정
        String aattCode = AATTdocument.getAdDetail();
        ApprovalAttendance foundAATT = approvalAttendanceRepository.findByAATTCode(aattCode);
        LocalDate newDate = LocalDate.from(foundAATT.getAattStart());
        System.out.println("비동기 근무일지의 날짜 : " + newDate);

        newAttendanceRequest.setAtdDate(newDate);

        // 신청한 시간 설정
        LocalTime newAtdStartTime = foundAATT.getAattStart().toLocalTime();
        newAttendanceRequest.setAtdStartTime(newAtdStartTime);

        LocalTime newAtdEndTime = foundAATT.getAattEnd().toLocalTime();
        newAttendanceRequest.setAtdEndTime(newAtdEndTime);

        /* ------------------------------------ 근무 상태 설정 ------------------------------------ */

        // 종류 선택
        String sort  = foundAATT.getAattSort();

        switch (sort) {

            case "외근" -> {
                AttendanceStatus defaultStatus = attendanceStatusRepository.findById(9)
                        .orElseThrow(() -> new EntityNotFoundException("기본 근무 상태를 찾을 수 없습니다."));
                newAttendanceRequest.setAttendanceStatus(defaultStatus);

                // DTO 객체를 Entity로 매핑하여 저장
                Attendance newAttendance = modelMapper.map(newAttendanceRequest, Attendance.class);
                attendanceRepository.save(newAttendance);
            }
            case "재택" -> {
                AttendanceStatus defaultStatus = attendanceStatusRepository.findById(13)
                        .orElseThrow(() -> new EntityNotFoundException("기본 근무 상태를 찾을 수 없습니다."));
                newAttendanceRequest.setAttendanceStatus(defaultStatus);

                // DTO 객체를 Entity로 매핑하여 저장
                Attendance newAttendance = modelMapper.map(newAttendanceRequest, Attendance.class);
                attendanceRepository.save(newAttendance);
            } case "휴일근무" -> {
                AttendanceStatus defaultStatus = attendanceStatusRepository.findById(1)
                        .orElseThrow(() -> new EntityNotFoundException("기본 근무 상태를 찾을 수 없습니다."));
                newAttendanceRequest.setAttendanceStatus(defaultStatus);

                // DTO 객체를 Entity로 매핑하여 저장
                Attendance newAttendance = modelMapper.map(newAttendanceRequest, Attendance.class);
                attendanceRepository.save(newAttendance);

                // 초과근무로 기록
                registOverWork(AATTdocument);
            }
        }
    }

    /* 근무 일지 자동 생성 */
    @Scheduled(cron = "30 24 19 * * *") // 매일 오전 4시 00분에 실행
    @Transactional
    public void createDailyAttendanceRecord() {

        // 모든 사원 목록 조회
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {

            // 사원의 최신 근무기록 조회
            Attendance latestAttendance = attendanceRepository.findTopByOrderByAtdCodeDesc();

            // 새로운 근무기록 생성을 위한 DTO 객체 생성
            AttendanceRegistRequest newAttendanceRequest = new AttendanceRegistRequest();

            // 오늘의 날짜 설정
            LocalDate currentDate = LocalDate.now();
            newAttendanceRequest.setAtdDate(currentDate);

            // 근무일지 코드 생성
            int atdCode = 1; // 기본값으로 설정

            if (latestAttendance != null) {
                atdCode = latestAttendance.getAtdCode() + 1;
            }
            newAttendanceRequest.setAtdCode(atdCode);

            // 지정 날짜가 있는지 조회
            List<DefaultSchedule> defaultScheduleByDate = defaultScheduleRepository.findByDate(currentDate);

            System.out.println("지정 날짜가 있는 지정 출퇴근시간" + defaultScheduleByDate);

            if (!defaultScheduleByDate.isEmpty()) {

                System.out.println("@@@ 지정 날짜가 있는 근태 기록 등록 시작 @@@");

                // 사원의 직책 정보 조회
                String empTitle = employee.getTitle().getTitle_code();

                // 직책이 책임자라면
                switch (empTitle) {

                    case "T1" -> {
                        newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                        newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                    }

                    // 직책이 부서장이라면
                    case "T2" -> {

                        // 사원의 부서 정보 조회
                        Department department = employee.getDepartment();
                        String deptCode = department.getDept_code();

                        Department departmentTitle = departmentRepository.findByDeptCode(deptCode);
                        String deptTitle = departmentTitle.getDept_title();

                        System.out.println("팀명 : " + deptTitle);

                        // 사원의 지정 출퇴근시간 조회
                        DefaultSchedule scheduleByEmpCode = defaultScheduleRepository.findByEmployeeAndDate(employee, currentDate);

                        // 사원번호가 존재한다면
                        if (scheduleByEmpCode != null) {
                            newAttendanceRequest.setAtdStartTime(scheduleByEmpCode.getAtdStartTime());
                            newAttendanceRequest.setAtdEndTime(scheduleByEmpCode.getAtdEndTime());

                            //사원번호가 존재하지 않는다면
                        } else {

                            // 사원번호가 존재하지 않는 오늘 날짜의 리스트 조회
                            List<DefaultSchedule> scheduleByEmpCodeIsNull = defaultScheduleRepository.findByEmployeeIsNullAndDate(currentDate);

                            System.out.println("사원번호가 존재하지 않는 오늘 날짜의 리스트 조회 : " + scheduleByEmpCodeIsNull);


                            // 사원의 부서 및 하위 부서에 따른 지정 출퇴근시간 조회
                            DefaultSchedule matchingSchedule = null;

                            for (DefaultSchedule schedule : scheduleByEmpCodeIsNull) {
                                if (schedule.getDeptTitle() != null && schedule.getDeptTitle().equals(deptTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                }
                            }

                            if (matchingSchedule != null) {
                                newAttendanceRequest.setAtdStartTime(matchingSchedule.getAtdStartTime());
                                newAttendanceRequest.setAtdEndTime(matchingSchedule.getAtdEndTime());
                            } else {
                                // 지정 출퇴근시간이 없을 경우 기본값 설정
                                newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                                newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                            }
                        }

                        // 직책이 부서장이라면
                    }
                    case "T4" -> {
                        // 사원의 부서 정보 조회
                        Department department = employee.getDepartment();
                        String deptCode = department.getDept_code();

                        Department departmentTitle = departmentRepository.findByDeptCode(deptCode);
                        String deptTitle = departmentTitle.getDept_title();

                        System.out.println("팀명 : " + deptTitle);

                        // 사원의 하위 부서 정보 조회
                        String parTitle = null;

                        DeptRelations subDeptRelations = deptRelationsRepository.findBySubDeptCode(deptCode);

                        if (subDeptRelations != null) {
                            Department subDepartment = subDeptRelations.getParentDepartment();
                            parTitle = subDepartment.getDept_title();
                        }

                        System.out.println("상위 부서 : " + parTitle);

                        // 사원의 지정 출퇴근시간 조회
                        DefaultSchedule scheduleByEmpCode = defaultScheduleRepository.findByEmployeeAndDate(employee, currentDate);

                        // 사원번호가 존재한다면
                        if (scheduleByEmpCode != null) {
                            newAttendanceRequest.setAtdStartTime(scheduleByEmpCode.getAtdStartTime());
                            newAttendanceRequest.setAtdEndTime(scheduleByEmpCode.getAtdEndTime());

                            //사원번호가 존재하지 않는다면
                        } else {

                            // 사원번호가 존재하지 않는 오늘 날짜의 리스트 조회
                            List<DefaultSchedule> scheduleByEmpCodeIsNull = defaultScheduleRepository.findByEmployeeIsNullAndDate(currentDate);

                            System.out.println("사원번호가 존재하지 않는 오늘 날짜의 리스트 조회 : " + scheduleByEmpCodeIsNull);


                            // 사원의 부서 및 하위 부서에 따른 지정 출퇴근시간 조회
                            DefaultSchedule matchingSchedule = null;

                            for (DefaultSchedule schedule : scheduleByEmpCodeIsNull) {
                                if (schedule.getParTitle().equals(parTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                }
                            }

                            if (matchingSchedule != null) {
                                newAttendanceRequest.setAtdStartTime(matchingSchedule.getAtdStartTime());
                                newAttendanceRequest.setAtdEndTime(matchingSchedule.getAtdEndTime());
                            } else {
                                // 지정 출퇴근시간이 없을 경우 기본값 설정
                                newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                                newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                            }
                        }
                    }
                    // 직책이 팀장이거나 팀원이라면
                    case "T5", "T6" -> {

                        // 사원의 부서 정보 조회
                        Department department = employee.getDepartment();
                        String deptCode = department.getDept_code();

                        Department departmentTitle = departmentRepository.findByDeptCode(deptCode);
                        String deptTitle = departmentTitle.getDept_title();

                        System.out.println("팀명 : " + deptTitle);

                        // 사원의 하위 부서 정보 조회
                        String subTitle = null;
                        String parTitle = null;

                        DeptRelations subDeptRelations = deptRelationsRepository.findBySubDeptCode(deptCode);

                        if (subDeptRelations != null) {
                            Department subDepartment = subDeptRelations.getParentDepartment();
                            subTitle = subDepartment.getDept_title();
                            String subCode = subDepartment.getDept_code();

                            if (subDepartment.getParentDepartments() != null) {
                                DeptRelations parDeptRelations = deptRelationsRepository.findBySubDeptCode(subCode);

                                if (parDeptRelations != null) {
                                    Department parentDepartment = parDeptRelations.getParentDepartment();
                                    parTitle = parentDepartment.getDept_title();
                                }
                            }
                        }

                        System.out.println("하위 부서 (사원 팀의 상위 부서): " + subTitle);
                        System.out.println("상위 부서 (하위 부서의 상위 부서): " + parTitle);

                        // 사원의 지정 출퇴근시간 조회
                        DefaultSchedule scheduleByEmpCode = defaultScheduleRepository.findByEmployeeAndDate(employee, currentDate);

                        // 사원번호가 존재한다면
                        if (scheduleByEmpCode != null) {
                            newAttendanceRequest.setAtdStartTime(scheduleByEmpCode.getAtdStartTime());
                            newAttendanceRequest.setAtdEndTime(scheduleByEmpCode.getAtdEndTime());

                            //사원번호가 존재하지 않는다면
                        } else {

                            // 사원번호가 존재하지 않는 오늘 날짜의 리스트 조회
                            List<DefaultSchedule> scheduleByEmpCodeIsNull = defaultScheduleRepository.findByEmployeeIsNullAndDate(currentDate);

                            System.out.println("사원번호가 존재하지 않는 오늘 날짜의 리스트 조회 : " + scheduleByEmpCodeIsNull);


                            // 사원의 부서 및 하위 부서에 따른 지정 출퇴근시간 조회
                            DefaultSchedule matchingSchedule = null;

                            for (DefaultSchedule schedule : scheduleByEmpCodeIsNull) {
                                if (schedule.getDeptTitle() != null && schedule.getDeptTitle().equals(deptTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                } else if (schedule.getDeptTitle() == null && schedule.getSubTitle() != null && schedule.getSubTitle().equals(subTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                } else if (schedule.getDeptTitle() == null && schedule.getSubTitle() == null && schedule.getParTitle() != null && schedule.getParTitle().equals(parTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                }
                            }

                            // matchingSchedule가 null이 아닌 경우에만 지정 출퇴근시간을 설정
                            if (matchingSchedule != null) {
                                newAttendanceRequest.setAtdStartTime(matchingSchedule.getAtdStartTime());
                                newAttendanceRequest.setAtdEndTime(matchingSchedule.getAtdEndTime());
                            } else {
                                // 지정 출퇴근시간이 없을 경우 기본값 설정
                                newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                                newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                            }
                        }
                    }
                }

            } else {
                System.out.println("@@@ 지정 날짜가 없는 근태 기록 등록 시작 @@@");

                // 사원의 직책 정보 조회
                String empTitle = employee.getTitle().getTitle_code();

                // 직책이 책임자라면
                switch (empTitle) {

                    case "T1" -> {
                        newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                        newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                    }

                    // 직책이 부서장이라면
                    case "T2" -> {

                        // 사원의 부서 정보 조회
                        Department department = employee.getDepartment();
                        String deptCode = department.getDept_code();

                        Department departmentTitle = departmentRepository.findByDeptCode(deptCode);
                        String deptTitle = departmentTitle.getDept_title();

                        System.out.println("상위 부서 팀명 : " + deptTitle);

                        // 사원의 지정 출퇴근시간 조회
                        DefaultSchedule scheduleByEmpCode = defaultScheduleRepository.findByEmployeeAndDateIsNull(employee);

                        // 사원번호가 존재한다면
                        if (scheduleByEmpCode != null) {
                            newAttendanceRequest.setAtdStartTime(scheduleByEmpCode.getAtdStartTime());
                            newAttendanceRequest.setAtdEndTime(scheduleByEmpCode.getAtdEndTime());

                            //사원번호가 존재하지 않는다면
                        } else {

                            // 사원번호가 존재하지 않는 모든 리스트 조회
                            List<DefaultSchedule> scheduleByDateIsNull = defaultScheduleRepository.findByDateIsNull();

                            System.out.println("사원번호가 존재하지 않는 리스트 조회 : " + scheduleByDateIsNull);

                            // 사원의 부서 및 하위 부서에 따른 지정 출퇴근시간 조회
                            DefaultSchedule matchingSchedule = null;

                            for (DefaultSchedule schedule : scheduleByDateIsNull) {
                                if (schedule.getDeptTitle() != null && schedule.getDeptTitle().equals(deptTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                }
                            }

                            if (matchingSchedule != null) {
                                newAttendanceRequest.setAtdStartTime(matchingSchedule.getAtdStartTime());
                                newAttendanceRequest.setAtdEndTime(matchingSchedule.getAtdEndTime());
                            } else {
                                // 지정 출퇴근시간이 없을 경우 기본값 설정
                                newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                                newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                            }
                        }

                        // 직책이 부서장이라면
                    }
                    case "T4" -> {
                        // 사원의 부서 정보 조회
                        Department department = employee.getDepartment();
                        String deptCode = department.getDept_code();

                        Department departmentTitle = departmentRepository.findByDeptCode(deptCode);
                        String deptTitle = departmentTitle.getDept_title();

                        System.out.println("팀명 : " + deptTitle);

                        // 사원의 하위 부서 정보 조회
                        String parTitle = null;

                        DeptRelations subDeptRelations = deptRelationsRepository.findBySubDeptCode(deptCode);

                        if (subDeptRelations != null) {
                            Department subDepartment = subDeptRelations.getParentDepartment();
                            parTitle = subDepartment.getDept_title();
                        }

                        System.out.println("상위 부서 : " + parTitle);

                        // 사원의 지정 출퇴근시간 조회
                        DefaultSchedule scheduleByDateIsNull = defaultScheduleRepository.findByEmployeeAndDateIsNull(employee);

                        // 사원번호가 존재한다면
                        if (scheduleByDateIsNull != null) {
                            newAttendanceRequest.setAtdStartTime(scheduleByDateIsNull.getAtdStartTime());
                            newAttendanceRequest.setAtdEndTime(scheduleByDateIsNull.getAtdEndTime());

                            //사원번호가 존재하지 않는다면
                        } else {

                            // 사원번호와 날짜가 존재하지 않는 리스트 조회
                            List<DefaultSchedule> scheduleByEmpCodeIsNullAndDateIsNull = defaultScheduleRepository.findByEmployeeIsNullAndDateIsNull();

                            System.out.println("사원번호와 날짜가 존재하지 않는 리스트 조회 : " + scheduleByEmpCodeIsNullAndDateIsNull);


                            // 사원의 부서 및 하위 부서에 따른 지정 출퇴근시간 조회
                            DefaultSchedule matchingSchedule = null;

                            for (DefaultSchedule schedule : scheduleByEmpCodeIsNullAndDateIsNull) {
                                if (schedule.getParTitle().equals(parTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                }
                            }

                            if (matchingSchedule != null) {
                                newAttendanceRequest.setAtdStartTime(matchingSchedule.getAtdStartTime());
                                newAttendanceRequest.setAtdEndTime(matchingSchedule.getAtdEndTime());
                            } else {
                                // 지정 출퇴근시간이 없을 경우 기본값 설정
                                newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                                newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                            }
                        }
                    }
                    // 직책이 팀장이거나 팀원이라면
                    case "T5", "T6" -> {

                        // 사원의 부서 정보 조회
                        Department department = employee.getDepartment();
                        String deptCode = department.getDept_code();

                        Department departmentTitle = departmentRepository.findByDeptCode(deptCode);
                        String deptTitle = departmentTitle.getDept_title();

                        System.out.println("팀명 : " + deptTitle);

                        // 사원의 하위 부서 정보 조회
                        String subTitle = null;
                        String parTitle = null;

                        DeptRelations subDeptRelations = deptRelationsRepository.findBySubDeptCode(deptCode);

                        if (subDeptRelations != null) {
                            Department subDepartment = subDeptRelations.getParentDepartment();
                            subTitle = subDepartment.getDept_title();
                            String subCode = subDepartment.getDept_code();

                            if (subDepartment.getParentDepartments() != null) {
                                DeptRelations parDeptRelations = deptRelationsRepository.findBySubDeptCode(subCode);

                                if (parDeptRelations != null) {
                                    Department parentDepartment = parDeptRelations.getParentDepartment();
                                    parTitle = parentDepartment.getDept_title();
                                }
                            }
                        }

                        System.out.println("하위 부서 (사원 팀의 상위 부서): " + subTitle);
                        System.out.println("상위 부서 (하위 부서의 상위 부서): " + parTitle);

                        // 사원의 지정 출퇴근시간 조회
                        DefaultSchedule scheduleByEmpCode = defaultScheduleRepository.findByEmployeeAndDateIsNull(employee);

                        // 사원번호가 존재한다면
                        if (scheduleByEmpCode != null) {
                            newAttendanceRequest.setAtdStartTime(scheduleByEmpCode.getAtdStartTime());
                            newAttendanceRequest.setAtdEndTime(scheduleByEmpCode.getAtdEndTime());

                            //사원번호가 존재하지 않는다면
                        } else {

                            // 사원번호와 날짜가 존재하지 않는 리스트 조회
                            List<DefaultSchedule> scheduleByEmpCodeIsNullAndDateIsNull = defaultScheduleRepository.findByEmployeeIsNullAndDateIsNull();

                            System.out.println("사원번호와 날짜가 존재하지 않는 리스트 조회 : " + scheduleByEmpCodeIsNullAndDateIsNull);


                            // 사원의 부서 및 하위 부서에 따른 지정 출퇴근시간 조회
                            DefaultSchedule matchingSchedule = null;

                            for (DefaultSchedule schedule : scheduleByEmpCodeIsNullAndDateIsNull) {
                                if (schedule.getDeptTitle() != null && schedule.getDeptTitle().equals(deptTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                } else if (schedule.getDeptTitle() == null && schedule.getSubTitle() != null && schedule.getSubTitle().equals(subTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                } else if (schedule.getDeptTitle() == null && schedule.getSubTitle() == null && schedule.getParTitle() != null && schedule.getParTitle().equals(parTitle)) {
                                    matchingSchedule = schedule;
                                    break;
                                }
                            }

                            // matchingSchedule가 null이 아닌 경우에만 지정 출퇴근시간을 설정
                            if (matchingSchedule != null) {
                                newAttendanceRequest.setAtdStartTime(matchingSchedule.getAtdStartTime());
                                newAttendanceRequest.setAtdEndTime(matchingSchedule.getAtdEndTime());
                            } else {
                                // 지정 출퇴근시간이 없을 경우 기본값 설정
                                newAttendanceRequest.setAtdStartTime(LocalTime.of(9, 0));    // 오전 09:00 으로 설정
                                newAttendanceRequest.setAtdEndTime(LocalTime.of(18, 0));    // 오후 06:00 으로 설정
                            }
                        }
                    }
                }
            }

            /* ------------------------------------ 사원 코드 설정 ------------------------------------ */

            newAttendanceRequest.setEmployee(employee);

            /* ------------------------------------ 근무 상태 설정 ------------------------------------ */

            // 1. 우선 디폴트로 설정
            AttendanceStatus defaultStatus = attendanceStatusRepository.findById(1)
                    .orElseThrow(() -> new EntityNotFoundException("기본 근무 상태를 찾을 수 없습니다."));
            newAttendanceRequest.setAttendanceStatus(defaultStatus);

            // DTO 객체를 Entity로 매핑하여 우선 저장
            Attendance newAttendance = modelMapper.map(newAttendanceRequest, Attendance.class);
            attendanceRepository.save(newAttendance);

            /* ------------------------------------ 휴직/휴가 설정 ------------------------------------ */

            // 1. 휴직 기록 찾기 : Document 엔터티에서 afCode = 7, adStatus = '완료', empCode = '본인'인 기록 조회
            List<Document> documents = docRepository.findAfCodeAndAdStatusAndEmpCode(employee.getEmp_code());
            System.out.println("휴직 기록 존재 여부 : " + documents);

            // 리스트가 존재한다면
            if(documents != null) {

                for (Document document : documents) {
                    if (document.getAdDetail().contains("AP")) {

                        // 오늘 날짜에 해당하는 기록이 있는지 조회
                        Personal foundPersonal = personalRepository.findByApStartAndApEnd(currentDate);

                        // 휴직 기록이 있다면
                        if (foundPersonal != null) {

                            // 근태일지 조회
                            Attendance attendance = attendanceRepository.findByEmployeeAndAtdDate(employee, currentDate);

                            // 근무상태 조회
                            List<AttendanceStatus> attendanceStatusList = attendanceStatusRepository.findAll();
                            AttendanceStatus newAttendanceStatus = attendanceStatusList.stream()
                                    .filter(status -> status.getAtsCode() == 8) // 휴직 상태 코드를 찾아서
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("휴직 처리 실패")); // 예외처리 추가

                            // 근무상태 업데이트
                            attendance.updateAttendanceStatus(newAttendanceStatus);
                        }
                    }
                }
            }

            // 2. 휴가 기록 찾기
            DayOff foundDayOff = dayOffRepository.findByEmpCodeAndCurrentDate(employee, currentDate);
            System.out.println("휴가 기록 존재 여부 : " + foundDayOff);

            if (foundDayOff != null) {

                // 근태일지 조회
                Attendance attendance = attendanceRepository.findByEmployeeAndAtdDate(employee, currentDate);

                // 근무상태 조회
                List<AttendanceStatus> attendanceStatusList = attendanceStatusRepository.findAll();
                AttendanceStatus newAttendanceStatus = attendanceStatusList.stream()
                        .filter(status -> status.getAtsCode() == 7) // 휴가 상태 코드를 찾아서
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("휴가 처리 실패")); // 예외처리 추가

                // 근무상태 업데이트
                attendance.updateAttendanceStatus(newAttendanceStatus);
            }
        }
    }


    /* 출근시간 등록 */
    @Transactional
    public AttendanceRegistStartTimeRequest registAttendanceStartTime(int empCode) {
        // 사원 조회
        Employee employee = employeeRepository.findById(empCode)
                .orElseThrow(() -> new EntityNotFoundException("사원을 찾을 수 없습니다."));

        System.out.println(employee.getEmp_name());

        // 현재 날짜 계산
        LocalDate currentDate = LocalDate.now();

        // 근태일지 조회
        Attendance foundAttendance = attendanceRepository.findByEmployeeAndAtdDate(employee, currentDate);

        if (foundAttendance == null) {
            throw new EntityNotFoundException("근태 기록을 찾을 수 없습니다.");
        }

        // 이미 출근 시간이 등록된 경우 예외 처리
        if (foundAttendance.getStartTime() != null) {
            throw new RuntimeException("출근시간이 이미 등록되었습니다.");
        }

        // 현재 시간을 출근 시간으로 설정
        LocalTime currentTime = LocalTime.now();
        foundAttendance.updateStartTime(currentTime);

        // 업데이트된 근태 기록 저장
        attendanceRepository.save(foundAttendance);

        /* 근무상태 업데이트 */
        LocalTime atdStartTime = foundAttendance.getAtdStartTime();

        System.out.println("atdStartTime: " + atdStartTime);
        System.out.println("currentTime: " + currentTime);

        if (currentTime.equals(atdStartTime) || currentTime.isBefore(atdStartTime)) {

            // 근무상태 조회
            List<AttendanceStatus> attendanceStatusList = attendanceStatusRepository.findAll();
            AttendanceStatus newAttendanceStatus = attendanceStatusList.stream()
                    .filter(status -> status.getAtsCode() == 2) // 2 : 출근
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("출근 상태 업데이트 실패"));

            // 근무상태 업데이트
            foundAttendance.updateAttendanceStatus(newAttendanceStatus);

        } else if (currentTime.isAfter(atdStartTime)){

            // 근무상태 조회
            List<AttendanceStatus> attendanceStatusList = attendanceStatusRepository.findAll();
            AttendanceStatus newAttendanceStatus = attendanceStatusList.stream()
                    .filter(status -> status.getAtsCode() == 3) // 3 : 지각
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("출근 상태 업데이트 실패"));

            // 근무상태 업데이트
            foundAttendance.updateAttendanceStatus(newAttendanceStatus);

        }

        AttendanceRegistStartTimeRequest response = new AttendanceRegistStartTimeRequest();
        response.setStartTime(currentTime);

        return response;
    }

    /* 퇴근시간 등록 */
    @Transactional
    public AttendanceRegistEndTimeRequest registAttendanceEndTime(int empCode) {

        // 사원 조회
        Employee employee = employeeRepository.findById(empCode)
                .orElseThrow(() -> new EntityNotFoundException("사원을 찾을 수 없습니다."));

        // 현재 날짜 계산
        LocalDate currentDate = LocalDate.now();

        // 근태일지 조회
        Attendance foundAttendance = attendanceRepository.findByEmployeeAndAtdDate(employee, currentDate);

        // 근태 기록이 없는 경우
        if (foundAttendance == null) {
            throw new EntityNotFoundException("근태 기록을 찾을 수 없습니다.");
        }

        // 출근 시간이 등록되지 않은 경우 처리
        if (foundAttendance.getAtdStartTime() == null) {
            throw new EntityNotFoundException("출근 시간이 등록되지 않았습니다.");
        }

        // 이미 퇴근 시간이 등록된 경우 예외 처리
        if (foundAttendance.getEndTime() != null) {
            throw new RuntimeException("퇴근시간이 이미 등록되었습니다.");
        }

        // 현재 시간을 퇴근 시간으로 설정
        LocalTime endTime = LocalTime.now();

        // 현재 퇴근 시간과 출근 시간을 비교하여 1분 이내인지 확인
        if (endTime.isBefore(foundAttendance.getAtdStartTime().plusMinutes(1))) {
            throw new EntityNotFoundException("총 근무시간이 1분 이하일 수 없습니다.");
        }

        // 퇴근시간 업데이트
        foundAttendance.updateEndTime(endTime);

        // 업데이트된 근태 기록 저장
        attendanceRepository.save(foundAttendance);

        // 근무상태 조회
        List<AttendanceStatus> attendanceStatusList = attendanceStatusRepository.findAll();
        AttendanceStatus newAttendanceStatus = attendanceStatusList.stream()
                .filter(status -> status.getAtsCode() == 5) // 5 : 퇴근
                .findFirst()
                .orElseThrow(() -> new RuntimeException("출근 상태 업데이트 실패"));

        // 근무상태 업데이트
        foundAttendance.updateAttendanceStatus(newAttendanceStatus);

        // 응답 객체 생성 및 설정
        AttendanceRegistEndTimeRequest response = new AttendanceRegistEndTimeRequest();
        response.setEndTime(endTime);

        return response;
    }

    /* 개인 : 금주의 근태 기록 */
    @Transactional(readOnly = true)
    public List<AttendancesResponse> getMyAttendancesForCurrentWeek(int empCode) {
        LocalDate[] dateRange = DateUtils.getCurrentWeek();
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        System.out.println(startDate);
        System.out.println(endDate);

        return attendanceRepository.findByEmpCodeAndInDateRange(empCode, startDate, endDate);
    }

    /* 전체 : 금주의 근태 기록 */
    @Transactional(readOnly = true)
    public List<AttendancesResponse> getAttendancesForCurrentWeek() {
        LocalDate[] dateRange = DateUtils.getCurrentWeek();
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        System.out.println(startDate);
        System.out.println(endDate);

        return attendanceRepository.findAttendanceInDateRange(startDate, endDate);
    }

    /* 개인 : 모든 근태 기록 */
    public List<AttendancesResponse> findAllMyAttendances(int empCode) {
        List<Attendance> attendances = attendanceRepository.findAllByEmpCode(empCode);

        System.out.println(attendances);

        return attendances.stream()
                .map(AttendancesResponse::new)
                .collect(Collectors.toList());
    }

    /* 권한별 모든 근태 기록 */


    /* 전체 : 모든 근태 기록 */
    public List<AttendancesResponse> findAllAttendances() {
        List<Attendance> attendances = attendanceRepository.findAll();

        return attendances.stream()
                .map(AttendancesResponse::new)
                .collect(Collectors.toList());
    }

    /* 가장 최신 근태 기록 조회 */
    @Transactional(readOnly = true)
    public Attendance getLatestAttendanceRecord() {
        return attendanceRepository.findTopByOrderByAtdCodeDesc();
    }

    /* 오늘의 근태 기록 조회 : 전체 */
    public List<AttendancesResponse> getAllAttendancesForToday() {

        // 현재 날짜 계산
        LocalDate currentDate = LocalDate.now();

        // 근태일지 조회
        List<Attendance> foundAttendance = attendanceRepository.findByAtdDate(currentDate);

        // AttendancesResponse 객체로 변환하여 반환
        return foundAttendance.stream()
                .map(AttendancesResponse::new)
                .collect(Collectors.toList());
    }

    /* 오늘의 근태 기록 조회 : 개인 */
    public AttendancesResponse getAttendancesForToday(int empCode) {

        // 사원 조회
        Employee employee = employeeRepository.findById(empCode)
                .orElseThrow(() -> new EntityNotFoundException("사원을 찾을 수 없습니다."));


        // 현재 날짜 계산
        LocalDate currentDate = LocalDate.now();

        // 근태일지 조회
        Attendance foundAttendance = attendanceRepository.findByEmployeeAndAtdDate(employee, currentDate);

        // AttendancesResponse 객체로 변환하여 반환
        AttendancesResponse response = new AttendancesResponse(foundAttendance);

        return response;
    }

    /* 지정 출퇴근시간 등록 */
    @Transactional
    public void registDefaultSchedule(DefaultScheduleRequest request) {

        // 지정 출퇴근시간 조회
        List<DefaultSchedule> defaultSchedulesList = defaultScheduleRepository.findAll();

        /* 기존 내역 있는지 조회 */

        // 이미 존재하는 상위 부서(parTitle)가 있으면 에러
        List<DefaultSchedule> conflictingSchedules = defaultSchedulesList.stream()
                .filter(defaultSchedule -> defaultSchedule.getParTitle().equals(request.getParTitle()))
                .toList();
        if (!conflictingSchedules.isEmpty()) {
            throw new RuntimeException("해당 상위 부서가 이미 존재합니다.");
        }

        // 이미 존재하는 상위 부서 and 하위 부서(subTitle)가 있으면 에러
        conflictingSchedules = defaultSchedulesList.stream()
                .filter(defaultSchedule -> defaultSchedule.getParTitle().equals(request.getParTitle()) &&
                        defaultSchedule.getSubTitle().equals(request.getSubTitle()))
                .toList();
        if (!conflictingSchedules.isEmpty()) {
            throw new RuntimeException("해당 상위 부서와 하위 부서가 이미 존재합니다.");
        }

        // 이미 존재하는 상위 부서 and 하위 부서 and 팀(deptTitle)이 있으면 에러
        conflictingSchedules = defaultSchedulesList.stream()
                .filter(defaultSchedule -> defaultSchedule.getParTitle().equals(request.getParTitle()) &&
                        defaultSchedule.getSubTitle().equals(request.getSubTitle()) &&
                        defaultSchedule.getDeptTitle().equals(request.getDeptTitle()))
                .toList();
        if (!conflictingSchedules.isEmpty()) {
            throw new RuntimeException("해당 상위 부서, 하위 부서, 팀이 이미 존재합니다.");
        }

        // 이미 존재하는 사원(emp_Name)이 있으면 에러
        conflictingSchedules = defaultSchedulesList.stream()
                .filter(defaultSchedule -> defaultSchedule.getEmployee().getEmp_name().equals(request.getEmployee().getEmp_name()))
                .toList();
        if (!conflictingSchedules.isEmpty()) {
            throw new RuntimeException("해당 사원이 이미 존재합니다.");
        }


        DefaultSchedule defaultSchedule = DefaultSchedule.builder()
                .dsStartDate(request.getDsStartDate())
                .dsEndDate(request.getDsEndDate())
                .atdStartTime(request.getAtdStartTime())
                .atdEndTime(request.getAtdEndTime())
                .parTitle(request.getParTitle())
                .subTitle(request.getSubTitle())
                .deptTitle(request.getDeptTitle())
                .employee(request.getEmployee())
                .build();

        defaultScheduleRepository.save(defaultSchedule);
    }

    /* 파라미터를 통한 지정 출퇴근시간 조회 */
    public List<DefaultSchedule> findDefaultSchedules(String deptTitle, LocalTime startTime, LocalTime endTime) {
        return defaultScheduleRepository.findByDeptTitleAndAtdStartTimeAndAtdEndTime(deptTitle, startTime, endTime);
    }

    /* 모든 지정 출퇴근시간 조회 */
    public List<DefaultScheduleResponse> findAllDefaultSchedules() {
        List<DefaultSchedule> defaultSchedules = defaultScheduleRepository.findAll();

        return defaultSchedules.stream()
                .map(DefaultScheduleResponse::new)
                .collect(Collectors.toList());
    }

    /* 사원별 지정 출퇴근시간 조회 */
    public List<DefaultScheduleResponse> findMyDefaultSchedules(int empCode) {
        return null;
    }

    /* 지정 출퇴근시간 수정 */
    @Transactional
    public void updateDefaultSchedule(String deptTitle, LocalTime startTime, LocalTime endTime, Employee employee) {

        // deptCode와 employee의 empCode가 모두 일치하는 첫 번째 DefaultSchedule 조회
        DefaultSchedule deptSchedule = defaultScheduleRepository.findByDeptTitleAndEmployee(deptTitle, employee);

        if (deptSchedule != null) {
            // empCode와 deptCode 모두 일치하는 경우 단일 엔티티 업데이트
            deptSchedule.updateStartTime(startTime);
            deptSchedule.updateEndTime(endTime);
            defaultScheduleRepository.save(deptSchedule); // 엔티티 저장
        } else {
            // empCode와 deptCode 모두 일치하는 DefaultSchedule이 없는 경우
            // deptCode에 해당하는 모든 DefaultSchedule 조회
            List<DefaultSchedule> deptSchedules = defaultScheduleRepository.findAllByDeptTitle(deptTitle);

            if (!deptSchedules.isEmpty()) {
                // deptCode에 해당하는 모든 DefaultSchedule 일괄 업데이트
                for (DefaultSchedule schedule : deptSchedules) {
                    schedule.updateStartTime(startTime);
                    schedule.updateEndTime(endTime);
                }
                defaultScheduleRepository.saveAll(deptSchedules); // 엔티티들 일괄 저장
            } else {
                throw new RuntimeException("수정할 출퇴근시간이 존재하지 않습니다.");
            }
        }
    }

    /* 지정 출퇴근시간 삭제 */
    @Transactional
    public boolean deleteSchedule(int dsCode) {
        Optional<DefaultSchedule> optionalSchedule = defaultScheduleRepository.findById(dsCode);
        if (optionalSchedule.isPresent()) {
            defaultScheduleRepository.delete(optionalSchedule.get());
            return true;
        } else {
            return false;
        }
    }

    /* ------------------------------------ 초과근무 기록 ------------------------------------ */

    @Transactional
    // 초과근무 등록
    public void registOverWork(Document AATTdocument) {

        // 신청한 날짜 설정
        String aattCode = AATTdocument.getAdDetail();
        ApprovalAttendance foundAATT = approvalAttendanceRepository.findByAATTCode(aattCode);
        LocalDate newDate = LocalDate.from(foundAATT.getAattStart());

        // 초과근무 날에 해당되는 근무일지 조회
        Attendance foundAttendance = attendanceRepository.findByEmployeeAndAtdDate(AATTdocument.getEmployee(), newDate);

        // 초과근무 시간 설정
        LocalTime owStartTime = foundAATT.getAattStart().toLocalTime();
        LocalTime owEndTime = foundAATT.getAattEnd().toLocalTime();

        System.out.println("근무일지 코드 : " + foundAttendance.getAtdCode());

        if(foundAttendance.getOverWork() != null) {
            throw new RuntimeException("이미 초과근무 내역이 존재합니다.");
        }

        // 없다면 빌드 후 초과근무 테이블에 DB 저장
        OverWork overWork = OverWork.builder()
                .owDate(newDate)
                .owStartTime(owStartTime)
                .owEndTime(owEndTime)
                .employee(AATTdocument.getEmployee())
                .build();
        overWorkRepository.save(overWork);

        // 근무일지의 초과근무 정보 업데이트
        foundAttendance.updateOverWork(overWork);
    }

    public List<OverWorkResponse> findAllOverTimeWork() {
        List<OverWork> overTimeWorkList = overWorkRepository.findAll();

        return overTimeWorkList.stream()
                .map(OverWorkResponse::new)
                .collect(Collectors.toList());
    }

    /* ------------------------------------ 휴가  ------------------------------------ */

    // 휴가 일괄 생성
    @Scheduled(cron = "00 01 21 * * *") // 일자 설정
    @Transactional
    public void createDayOffRecord() {

        // 모든 사원 목록 조회
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            // 사원의 최신 휴가관리 목록 조회
            DayOffBalance latestDayOffBalance = dayOffBalanceRepository.findTopByOrderByDbCodeDesc();

            // 새로운 근무기록 생성을 위한 DTO 객체 생성
            DayOffBalanceRequest newDayOffBalance = new DayOffBalanceRequest();

            // 근무일지 코드 생성
            if (latestDayOffBalance != null) {
                newDayOffBalance.setDbCode(latestDayOffBalance.getDbCode() + 1);
            } else {
                newDayOffBalance.setDbCode(1);
            }

            // 부여수, 잔여수, 사용수, 사원코드 설정
            newDayOffBalance.setGranted(15.0);
            newDayOffBalance.setRemaining(15.0);
            newDayOffBalance.setDbUsed(0.0);
            newDayOffBalance.setEmployee(employee);

            // 부여날짜 설정
            LocalDate currentDate = LocalDate.now();
            newDayOffBalance.setDbInsertDate(currentDate);

            // DTO 객체를 Entity 로 매핑하여 저장
            DayOffBalance dayOffBalance = modelMapper.map(newDayOffBalance, DayOffBalance.class);
            dayOffBalanceRepository.save(dayOffBalance);
        }
    }

    // 휴가기록 조회 : 전체
    public List<DayOffResponse> findAllDayOff() {
        List<DayOff> dayOffList = dayOffRepository.findAll();

        return dayOffList.stream()
                .map(DayOffResponse::new)
                .collect(Collectors.toList());
    }

    // 휴가기록 조회 : 개인
    public List<DayOffResponse> findAllDayOffByEmpCode(int empCode) {
        List<DayOff> dayOffList = dayOffRepository.findAllByEmpCode(empCode);

        return dayOffList.stream()
                .map(DayOffResponse::new)
                .collect(Collectors.toList());
    }

    // 단일 휴가기록 조회 : 개인
    public DayOffResponse findDayOffResearch(int empCode, String doStartDate) {
        DayOff dayOff = dayOffRepository.findByEmpCode(empCode, doStartDate);

        DayOffResponse response = new DayOffResponse(dayOff);

        return response;
    }

    // 보유 휴가 조회 : 개인
    public DayOffBalanceResponse findAllDayOffBalanceByEmpCode(int empCode) {
        DayOffBalance dayOffBalanceList = dayOffBalanceRepository.findAllByEmpCode(empCode);

        DayOffBalanceResponse response = new DayOffBalanceResponse(dayOffBalanceList);

        return response;
    }

    @Transactional
    // 휴가 등록
    public void registDayOff(LocalDate doReportDate,
                             String doName, Double doUsed,
                             LocalDate doStartDate, LocalDate doEndDate,
                             LocalTime doStartTime, LocalTime doEndTime,
                             Employee employee) {

        // 휴가 기록 조회
        List<DayOff> myDayOff = dayOffRepository.findDoByStartDateOrDoEndDate(doStartDate, doEndDate);


        // 이미 있는 내역이라면 에러
        List<DayOff> conflictingDayOffs = myDayOff.stream()
                .filter(dayOff -> dayOff.getDoStartDate().equals(doStartDate) || dayOff.getDoEndDate().equals(doEndDate))
                .toList();
        if (!conflictingDayOffs.isEmpty()) {
            throw new RuntimeException("신청한 일자가 이미 존재합니다.");
        }

        // 보유 휴가 조회
        DayOffBalance myDayOffBalance = dayOffRepository.findMyDayOffBalanceByEmpCode(employee.getEmp_code());

        /* 휴가 기록 저장 */
        DayOff dayOff = DayOff.builder()
                .doReportDate(doReportDate)
                .doName(doName)
                .doUsed(doUsed)
                .doStartDate(doStartDate)
                .doEndDate(doEndDate)
                .doStartTime(doStartTime)
                .doEndTime(doEndTime)
                .granted(myDayOffBalance.getGranted())
                .dbUsed(myDayOffBalance.getDbUsed())
                .remaining(myDayOffBalance.getRemaining())
                .employee(employee)
                .build();

        dayOffRepository.save(dayOff);

        /* 기록 남긴 후, 보유 휴가 수정 */

        if (doUsed == 1) {
            myDayOffBalance.modifyGranted(1);
            myDayOffBalance.modifyDbUsed(1);
            myDayOffBalance.modifyRemaining(1);
        } else if (doUsed == 0.5) {
            myDayOffBalance.modifyGranted(0.5);
            myDayOffBalance.modifyDbUsed(0.5);
            myDayOffBalance.modifyRemaining(0.5);
        } else if (doUsed == 0.25) {
            myDayOffBalance.modifyGranted(0.25);
            myDayOffBalance.modifyDbUsed(0.25);
            myDayOffBalance.modifyRemaining(0.25);
        }

        // 변경사항을 데이터베이스에 반영
        dayOffBalanceRepository.save(myDayOffBalance);
    }


    // 연차 촉진 이메일 발송 서비스 (단체)
    @Scheduled(cron = "30 26 19 * * *") // 매월 1일에 발송
    public void autoSendDayOffEmail() throws MessagingException {

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        System.out.println("현월 : " + currentMonth);

        // 모든 사원 목록 조회
        List<Employee> employees = employeeRepository.findAll();

        for(Employee employee : employees) {

            // 연차 보유 갯수 조회
            DayOffBalance foundDayOffBalance = dayOffBalanceRepository.findRecipient(employee.getEmp_code());

            if(foundDayOffBalance != null) {
                // 소진연차를 위한 월 계산
                LocalDate insertDateMonth = foundDayOffBalance.getDbInsertDate(); // 부여된 일자
                int month = insertDateMonth.getMonthValue();
                System.out.println("부여된 월 : " + month);
                int resultMonth = 13 - month;
                System.out.println("소진 기준 월 : " + resultMonth);

                // 소진해야 할 연차 계산
                int result = (int) ((foundDayOffBalance.getGranted() / resultMonth) * currentMonth) + 1;
                System.out.println("소진해야 할 연차 : " + result);

                // 만약 소진해야할 연차보다 사용 연차가 적다면
                if(result > foundDayOffBalance.getDbUsed()) {
                    emailService.autoSendDayOffEmail(employee, result);

                    System.out.println("연차 촉진 이메일 발송 완료");
                }
            }
        }
    }

    // 연차 촉진 이메일 발송 서비스 (개별)
    public void sendDayOffEmail(int empCode) throws MessagingException {

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        System.out.println("현월 : " + currentMonth);

        // 연차 보유 갯수 조회
        DayOffBalance foundDayOffBalance = dayOffBalanceRepository.findRecipient(empCode);

        if(foundDayOffBalance != null) {
            // 소진연차를 위한 월 계산
            LocalDate insertDateMonth = foundDayOffBalance.getDbInsertDate(); // 부여된 일자
            int month = insertDateMonth.getMonthValue();
            System.out.println("부여된 월 : " + month);
            int resultMonth = 13 - month;
            System.out.println("소진 기준 월 : " + resultMonth);

            // 소진해야 할 연차 계산
            int result = (int) ((foundDayOffBalance.getGranted() / resultMonth) * currentMonth) + 1;
            System.out.println("소진해야 할 연차 : " + result);

            // employee
            Employee employee = employeeRepository.findByEmpCode(empCode);

            // 만약 소진해야할 연차보다 사용 연차가 적다면
            if(result > foundDayOffBalance.getDbUsed()) {
                emailService.sendDayOffEmail(employee, result);

                System.out.println("연차 촉진 이메일 발송 완료");
            }
        }
    }

    // 결재 현황 확인을 위한 서비스

    // 예외근무 현황
    public List<DocumentResponse> findBTDocList(int empCode) {

        List<DocumentResponse> btList = docRepository.findCurrentBTList(empCode);
        return btList;
    }

    // 초과근무 현황
    public List<DocumentResponse> findOWDocList(int empCode) {

        List<DocumentResponse> owList = docRepository.findCurrentOWList(empCode);
        return owList;
    }

    // 휴가신청 현황
    public List<DocumentResponse> findDODocList(int empCode) {

        List<DocumentResponse> doList = docRepository.findCurrentDOList(empCode);
        return doList;
    }

    // 월간 휴가신청 승인 현황
    public List<DocumentResponse> findDODoneDocList(int empCode) {

        LocalDateTime startDate = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()); // 이번 달 1일
        LocalDateTime endDate = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()); // 이번 달 말일

        // 이번 달 휴가 신청기록 조회
        List<ApprovalAttendance> listAATT = approvalAttendanceRepository.findByCurrentMonth(startDate, endDate);

        // 휴가 신청 코드를 담을 리스트
        List<String> AATTCode = listAATT.stream()
                .map(ApprovalAttendance::getAattCode)
                .collect(Collectors.toList());

        // 본인의 승인된 결재 문서 조회
        List<DocumentResponse> listDocAATT = docRepository.findByEmpCode(empCode);

        // AATTCode와 일치하는 DocumentResponse 객체들을 담을 리스트, 최대 5개 제한
        List<DocumentResponse> resultList = listDocAATT.stream()
                .filter(doc -> AATTCode.contains(doc.getAdDetail()))
                .limit(5)
                .collect(Collectors.toList());

        return resultList;
    }

    // 오늘 날짜의 근무 상태가 휴가, 출장, 교육, 훈련, 재택인 근무일지
    public List<AttendancesResponse> findAbsentee() {

        LocalDate today = LocalDate.now();

        List<AttendancesResponse> findAbsentee = attendanceRepository.findAbsentee(today);

        return findAbsentee;
    }


    // [메인] 이번달 생일인 사원
    public EmployeeListResponse findBirth() {
        List<Employee> employees = employeeRepository.findAll();
        System.out.println("모든 사원 목록 조회 완료");

        // 현재 날짜에서 이번 달의 월과 일을 추출
        LocalDate today = LocalDate.now();
        int thisDayOfMonth = today.getMonthValue();
        System.out.println("이번 달 : " + thisDayOfMonth);

        // 이번 달 생일인 사원 필터링 후 생일이 빠른 순으로 정렬
        List<Employee> birthdayEmployees = employees.stream()
                .filter(employee -> {
                    // 주민등록번호에서 월과 일 추출
                    int month = getMonthFromSocialSecurityNo(employee.getSocial_security_no());
                    int day = getDayFromSocialSecurityNo(employee.getSocial_security_no());
                    System.out.println("월 : " + month);
                    System.out.println("일 : " + day);

                    // 생일이 이번 달인지 확인
                    return month == thisDayOfMonth;
                })
                .sorted((e1, e2) -> {
                    // 생일이 빠른 순으로 정렬
                    int day1 = getDayFromSocialSecurityNo(e1.getSocial_security_no());
                    int day2 = getDayFromSocialSecurityNo(e2.getSocial_security_no());
                    return day1 - day2;
                })
                .collect(Collectors.toList());

        // EmployeeListResponse로 변환하여 반환
        return getEmployeeList(birthdayEmployees);
    }

    // 주민등록번호에서 월을 추출하는 메소드
    private int getMonthFromSocialSecurityNo(String socialSecurityNo) {
        if (socialSecurityNo != null && socialSecurityNo.length() >= 6) {
            return Integer.parseInt(socialSecurityNo.substring(2, 4));
        }
        return -1; // 예외 처리 필요
    }

    // 주민등록번호에서 일을 추출하는 메소드
    private int getDayFromSocialSecurityNo(String socialSecurityNo) {
        if (socialSecurityNo != null && socialSecurityNo.length() >= 6) {
            return Integer.parseInt(socialSecurityNo.substring(4, 6));
        }
        return -1; // 예외 처리 필요
    }

    // 공지사항 조회
    public List<PostEntity> InboardList(int num) {

        List<PostEntity> posts = postService.findNotice(num);
        return posts;
    }

    // 쪽지 조회
    public List<ReceiveResponse> getReceiveMessage(int empCode) {

        List<Message> receiveList = messageRepository.findByEmpRev_EmpCodeAndRN(empCode);

        return receiveList.stream()
                .map(ReceiveResponse::getReceiveMessage)
                .collect(Collectors.toList());
    }
}


