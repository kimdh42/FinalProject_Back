package synergyhubback.employee.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import synergyhubback.approval.domain.entity.AppointDetail;
import synergyhubback.approval.domain.entity.ApprovalAppoint;
import synergyhubback.approval.domain.repository.AppointDetailRepository;
import synergyhubback.approval.domain.repository.ApprovalAppointRepository;
import synergyhubback.auth.dto.LoginDto;
import synergyhubback.common.address.service.EmailService;
import synergyhubback.common.exception.NotFoundException;
import synergyhubback.employee.domain.entity.*;
import synergyhubback.employee.domain.repository.*;
import synergyhubback.employee.dto.request.*;
import synergyhubback.employee.dto.response.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static synergyhubback.common.exception.type.ExceptionCode.*;
import static synergyhubback.employee.dto.response.EmployeeListResponse.getEmployeeList;


@Service("employeeService")
@Transactional
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final CertificateRepository certificateRepository;
    private final SchoolInfoRepository schoolInfoRepository;
    private final DepartmentRepository departmentRepository;
    private final DeptRelationsRepository deptRelationsRepository;
    private final TitleRepository titleRepository;
    private final PositionRepository positionRepository;
    private final DetailByEmpRegistRepository detailByEmpRegistRepository;
    private final EmailService emailService;
    private final AppointDetailRepository appointDetailRepository;
    private final ApprovalAppointRepository approvalAppointRepository;
    private final BankRepository bankRepository;
    private final Path profileImgRoot = Paths.get("C:/SynergyHub/ProfileImg/");

    @Transactional(readOnly = true)
    public LoginDto findByEmpCode(int emp_code) {

        try {

            Employee employee = employeeRepository.findByEmpCode(emp_code);
            return LoginDto.from(employee);

        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            System.out.println("해당 아이디가 존재하지 않습니다.");
        }

        return null;
    }

    // 이재현 로그인 관련 service 로직 생성
    public void updateRefreshToken(int emp_code, String refreshToken) {
        try {

            Employee employee = employeeRepository.findByEmpCode(emp_code);
            employee.updateRefreshToken(refreshToken);
        } catch ( Exception e) {
            System.out.println(e);
        }

        System.out.println("refresh Token 발급");

    }

    // 이재현 로그인 관련 service 로직 생성
    @Transactional(readOnly = true)
    public LoginDto findByRefreshToken(String refreshToken) {

        Employee employee = employeeRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_REFRESH_TOKEN));

        return LoginDto.from(employee);
    }

    public void empsRegist(List<EmployeeRegistRequest> employeeRegistRequests, String detailErdNum, String detailErdTitle, int empCode, LocalDate detailErdRegistdate) throws MessagingException {
        for (EmployeeRegistRequest employeeRegistRequest : employeeRegistRequests) {

            /* 사원코드 생성 (yyyyMM+1) */
            String hireYearMonth = employeeRegistRequest.getHire_date().format(DateTimeFormatter.ofPattern("yyyyMM"));
            long count = employeeRepository.countByHireYearMonth(hireYearMonth);
            String newEmpCode = hireYearMonth + (count + 1);

            String erdWriter = employeeRepository.findEmpNameById(empCode);

            Department department = departmentRepository.findByDeptCode(employeeRegistRequest.getDept_code());
            Title title = titleRepository.findByTitleCode(employeeRegistRequest.getTitle_code());
            Position position = positionRepository.findByPositionCode(employeeRegistRequest.getPosition_code());

            /* Employee 객체 생성 */
            Employee newEmp = Employee.regist(
                    Integer.parseInt(newEmpCode),
                    employeeRegistRequest.getEmp_name(),
                    passwordEncoder.encode(newEmpCode),
                    employeeRegistRequest.getSocial_security_no(),
                    employeeRegistRequest.getEmail(),
                    employeeRegistRequest.getHire_date(),
                    employeeRegistRequest.getEmp_status()
            );

            newEmp.setDepartment(department);
            newEmp.setTitle(title);
            newEmp.setPosition(position);

            /* DetailByEmpRegist 객체 생성 */
            DetailByEmpRegist detailByEmpRegist = new DetailByEmpRegist(
                    detailErdNum,
                    detailErdTitle,
                    erdWriter,
                    detailErdRegistdate,
                    newEmp
            );

            /* Employee 객체에 DetailByEmpRegist 추가 */
            newEmp.addEmpRegistDetail(detailByEmpRegist);

            /* 저장 */
            employeeRepository.save(newEmp);

            /* 아이디, 비밀번호 이메일 전송 */
            emailService.sendNewEmp(newEmp);
        }
    }

    public List<DetailByEmpRegistResponseGroup> getEmpRegistList() {

        List<DetailByEmpRegist> detailByEmpRegists = detailByEmpRegistRepository.findAll();

        Map<String, List<DetailByEmpRegist>> groupedByInfo = detailByEmpRegists.stream()
                .collect(Collectors.groupingBy(empRegist ->
                        empRegist.getErd_num() + "-" +
                                empRegist.getErd_title() + "-" +
                                empRegist.getErd_writer() + "-" +
                                empRegist.getErd_registdate()));

        Map<String, List<DetailByEmpRegist>> sortedGroupedByInfo = groupedByInfo.entrySet().stream()
                .sorted(Map.Entry.<String, List<DetailByEmpRegist>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        List<DetailByEmpRegistResponseGroup> responseGroups = sortedGroupedByInfo.entrySet().stream()
                .map(entry -> {
                    List<DetailByEmpRegistResponse> responses = entry.getValue().stream()
                            .sorted(Comparator.comparing(DetailByEmpRegist::getErd_code))
                            .map(DetailByEmpRegistResponse::getEmpRegistList)
                            .collect(Collectors.toList());

                    DetailByEmpRegist firstElement = entry.getValue().get(0);

                    return new DetailByEmpRegistResponseGroup(
                            firstElement.getErd_num(),
                            firstElement.getErd_title(),
                            firstElement.getErd_writer(),
                            firstElement.getErd_registdate(),
                            responses
                    );
                })
                .collect(Collectors.toList());

        return responseGroups;
    }

    public EmpRegistDetailListResponse getEmpsRegistListDetail(String erdNum) {

        List<DetailByEmpRegist> detailByEmpRegists = detailByEmpRegistRepository.findEmpRegistListDetail(erdNum);

        DetailByEmpRegist detailByEmpRegist = detailByEmpRegists.get(0);

        return EmpRegistDetailListResponse.fromEntity(detailByEmpRegist.getErd_num(), detailByEmpRegist.getErd_title(), detailByEmpRegists);
    }

    public MyInfoResponse getMyInfo(int empCode) {


        Employee employee = employeeRepository.findByEmpCode(empCode);


        return MyInfoResponse.getMyInfo(employee);

    }

    public EmployeeResponse updateMyInfo(int empCode, EmployeeUpdateRequest employeeUpdateRequest) {
        Employee employee = employeeRepository.findById(empCode)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EMPLOYEE));

        // 현재 비밀번호가 올바른지 확인
        if (employeeUpdateRequest.getEmp_pass() != null && !passwordEncoder.matches(employeeUpdateRequest.getEmp_pass(), employee.getEmp_pass())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호가 존재할 경우 업데이트
        if (employeeUpdateRequest.getNew_emp_pass() != null && !employeeUpdateRequest.getNew_emp_pass().isEmpty()) {
            String encodedNewPassword = passwordEncoder.encode(employeeUpdateRequest.getNew_emp_pass());
            employee.updatePassword(encodedNewPassword);
        }

        // 기타 정보 업데이트
        employee.update(
                employeeUpdateRequest.getEmail(),
                employeeUpdateRequest.getPhone(),
                employeeUpdateRequest.getAddress(),
                null, // 여기서는 이미 비밀번호 업데이트가 처리되었으므로 null 전달
                employeeUpdateRequest.getBank_name() != null ? bankRepository.findByName(employeeUpdateRequest.getBank_name())
                        .orElseThrow(() -> new NotFoundException(NOT_FOUND_INFORMATION)) : null,
                employeeUpdateRequest.getAccount_num()
        );

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(updatedEmployee);
    }

    public List<EmployeeResponse> getEmployeeAll() {

        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }


    public RecordCardResponse getRecordCard(int empCode) {

        Employee employee = employeeRepository.findByEmpCode(empCode);

        List<SchoolInfo> schoolInfos = schoolInfoRepository.findAllByEmpCode(empCode);

        List<Certificate> certificates = certificateRepository.findAllByEmpCode(empCode);


        return RecordCardResponse.getRecordCard(employee, schoolInfos, certificates);
    }

    public void registRecordCard(RegistRecordCardRequest request) {

        // 직원 코드로 직원 정보 조회
        Employee employee = employeeRepository.findById(request.getEmp_code())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_INFORMATION));

        // 학력 정보 등록
        if (request.getSchoolInfos() != null) {
            List<SchoolInfo> schoolInfos = request.getSchoolInfos().stream()
                    .map(sch -> new SchoolInfo(
                            sch.getSch_name(),
                            sch.getGrad_status(),
                            sch.getEnrole_date(),
                            sch.getGrad_date(),
                            sch.getMajor(),
                            sch.getDay_n_night(),
                            sch.getLocation(),
                            request.getEmp_code()
                    ))
                    .collect(Collectors.toList());
            schoolInfoRepository.saveAll(schoolInfos);
        }

        // 자격증 정보 등록
        if (request.getCertificates() != null) {
            List<Certificate> certificates = request.getCertificates().stream()
                    .map(cer -> new Certificate(
                            cer.getCer_code(),
                            cer.getCer_name(),
                            cer.getCer_score(),
                            cer.getCer_date(),
                            cer.getCer_num(),
                            cer.getIss_organ(),
                            request.getEmp_code()
                    ))
                    .collect(Collectors.toList());
            certificateRepository.saveAll(certificates);
        }
    }

    public void updateRecordCard(RegistRecordCardRequest registRecordCardRequest) {
        // 학력 정보 업데이트
        if (registRecordCardRequest.getSchoolInfos() != null) {
            for (RegistSchoolInfoRequest schoolInfoRequest : registRecordCardRequest.getSchoolInfos()) {
                SchoolInfo schoolInfo = schoolInfoRepository.findById(schoolInfoRequest.getSch_code())
                        .orElseThrow(() -> new NotFoundException(NOT_FOUND_INFORMATION));

                schoolInfo.updateSchoolInfo(schoolInfoRequest);
            }
        }

        // 자격증 정보 업데이트
        if (registRecordCardRequest.getCertificates() != null) {
            for (RegistCertificateRequest certificateRequest : registRecordCardRequest.getCertificates()) {
                Certificate certificate = certificateRepository.findById(certificateRequest.getCer_code())
                        .orElseThrow(() -> new NotFoundException(NOT_FOUND_INFORMATION));

                certificate.updateCertificateInfo(certificateRequest);
            }
        }
    }

    public EmployeeListResponse employeeList(int empCode) {

        System.out.println("findDeptCodeByEmpCode : " + empCode);
        String dept_code = employeeRepository.findDeptCodeByEmpCode(empCode);

        System.out.println("dept code : " + dept_code);
        List<Employee> employees = employeeRepository.findAllByDeptCode(dept_code);

        System.out.println("emp found : " + employees.size());

        return getEmployeeList(employees);
    }


    public RecordCardResponse getTeamRecordCard(int logInEmpCode, int empCode) throws IllegalAccessException, NotFoundException {

        Employee logInEmployee = employeeRepository.findByEmpCode(logInEmpCode);

        Employee targetEmployee = employeeRepository.findByEmpCode(empCode);

//        if (!isAuthorized(logInEmployee, targetEmployee)) {
//            throw new IllegalAccessException("조회할 권한이 없습니다.");
//        }

        List<SchoolInfo> schoolInfos = schoolInfoRepository.findAllByEmpCode(empCode);

        List<Certificate> certificates = certificateRepository.findAllByEmpCode(empCode);

        return RecordCardResponse.getRecordCard(targetEmployee, schoolInfos, certificates);

    }

    private boolean isAuthorized(Employee logInEmployee, Employee targetEmployee) {

        String logInEmpTitleCode = logInEmployee.getTitle().getTitle_code();

        if (!"T1".equals(logInEmpTitleCode) &&
                !"T2".equals(logInEmpTitleCode) &&
                !"T4".equals(logInEmpTitleCode) &&
                !"T5".equals(logInEmpTitleCode)) {
            return false;
        }

        String logInDeptCode = logInEmployee.getDepartment().getDept_code();
        String targetDeptCode = targetEmployee.getDepartment().getDept_code();

        if (logInDeptCode.equals(targetDeptCode)) {
            return true;
        }

        for (DeptRelations relation : logInEmployee.getDepartment().getSubDepartments()) {
            if (relation.getSubDepartment().getDept_code().equals(targetDeptCode)) {
                return true;
            }
        }
        return false;
    }



    public void registDept(RegistDeptRequest registDeptRequest) {

        String newDeptCode = nextDeptCode();

        Department department = new Department(
                newDeptCode,
                registDeptRequest.getDept_title(),
                LocalDate.now(),
                null,
                new HashSet<>(),
                new HashSet<>()
        );

        departmentRepository.save(department);
    }

    private String nextDeptCode() {

        List<Department> allDeptCodes = departmentRepository.findAll();

        int nextNumber = 1;
        for (Department dept : allDeptCodes) {
            String numberPart = dept.getDept_code().substring(1);
            try {
                int currentNumber = Integer.parseInt(numberPart);
                if (currentNumber >= nextNumber) {
                    nextNumber = currentNumber + 1;
                }
            } catch (NumberFormatException e) {

            }
        }

        return "D" + nextNumber;
    }

    public void registDeptRelations(RegistDeptRelationsRequest registDeptRelationsRequest) {

        Department parentDepartment = registDeptRelationsRequest.getParentDepartment();
        Department subDepartment = registDeptRelationsRequest.getSubDepartment();

        DeptRelations existingRelation = deptRelationsRepository.findByParentDepartmentAndSubDepartment(parentDepartment, subDepartment);

        if(existingRelation != null) {
            throw new IllegalArgumentException("이미 등록된 부서 관계 입니다.");
        }

        DeptRelations deptRelations = new DeptRelations(parentDepartment, subDepartment);

        deptRelationsRepository.save(deptRelations);
    }

    public List<EmpTitleListResponse> getEmpTitleList() {
        List<Title> empTitleList = titleRepository.findAll();

        return empTitleList.stream().map(EmpTitleListResponse::from).toList();
    }

    public List<GetPositionNameResponse> getPositionList() {
        List<Position> empPositionList = positionRepository.findAll();

        return empPositionList.stream().map(GetPositionNameResponse::from).toList();
    }

    public void modifyDeptRelations(int dept_relations_code, Department parentDepartment, Department subDepartment) {

        DeptRelations deptRelations = deptRelationsRepository.findById(dept_relations_code)
                .orElseThrow(() -> new NotFoundException(DEPT_RELATIONS_NOT_FOUND));

        deptRelations.setParentDepartment(parentDepartment);
        deptRelations.setSubDepartment(subDepartment);

        deptRelationsRepository.save(deptRelations);

    }

    public void deleteDeptRelations(RegistDeptRelationsRequest registDeptRelationsRequest) {

        Department parentDepartment = registDeptRelationsRequest.getParentDepartment();

        Department subDepartment = registDeptRelationsRequest.getSubDepartment();

        DeptRelations deptRelations = deptRelationsRepository.findByParentDepartmentAndSubDepartment(parentDepartment, subDepartment);

        if (deptRelations != null) {

            deptRelationsRepository.delete(deptRelations);
        }

    }

    public List<GetDeptTitleResponse> getDepartments() {

        List<Department> getDepartments = departmentRepository.findAllAndRelations();

        return getDepartments.stream()
                .map(department -> new GetDeptTitleResponse(
                        department.getDept_code(),
                        department.getDept_title(),
                        department.getSubDepartments().stream()
                                .map(sub -> sub.getSubDepartment().getDept_code())
                                .collect(Collectors.toList()),
                        department.getParentDepartments().stream()
                                .map(par -> par.getParentDepartment().getDept_code())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public DepartmentResponse getDeptDetailList(String dept_code) {

        Department department = departmentRepository.findDeptDetailByDeptCode(dept_code);

        String deptManagerName = getDeptManagerName(department);

        List<String> parDeptTitles = department.getParentDepartments().stream()
                .map(par -> par.getParentDepartment().getDept_title())
                .collect(Collectors.toList());

        List<String> subDeptTitles = department.getSubDepartments().stream()
                .map(sub -> sub.getSubDepartment().getDept_title())
                .collect(Collectors.toList());

        int numOfTeamMember = getNumOfTeamMembers(department);

        List<String> teamMemberNames = getTeamMemberNames(department);

        String parentDeptManagerName = deptRelationsRepository.findParentDepartmentManagerBySubDeptCode(department.getDept_code());

        DeptDetailResponse deptDetailResponse = DeptDetailResponse.getDeptDetail(
                department,
                deptManagerName,
                parDeptTitles,
                subDeptTitles,
                numOfTeamMember,
                teamMemberNames,
                parentDeptManagerName
        );

        return DepartmentResponse.from(List.of(deptDetailResponse));
    }

    private String getDeptManagerName(Department department) {
        return employeeRepository.findManagerByDeptCode(department.getDept_code());
    }

    private int getNumOfTeamMembers(Department department) {
        return employeeRepository.countTeamMembersByDeptCode(department.getDept_code());
    }

    private List<String> getTeamMemberNames(Department department) {
        return employeeRepository.findTeamMemberNamesByDeptCode(department.getDept_code());
    }


    public List<EmployeeResponse> getOrg() {
        List<Employee> employees = employeeRepository.findAll();

        // 전체 부서 관계 정보를 조회
        List<DeptRelationsResponse> deptRelationsResponses = deptRelationsRepository.findAll().stream()
                .map(DeptRelationsResponse::fromEntity)
                .collect(Collectors.toList());

        return employees.stream()
                .map(employee -> {
                    EmployeeResponse employeeResponse = EmployeeResponse.fromEntity(employee);

                    // 부서 관계 설정
                    setDepartmentRelations(employeeResponse, deptRelationsResponses);

                    return employeeResponse;
                })
                .collect(Collectors.toList());
    }

    private void setDepartmentRelations(EmployeeResponse employeeResponse, List<DeptRelationsResponse> deptRelationsResponses) {
        String empDeptCode = employeeResponse.getDept_code();

        // 부서의 상위 부서와 하위 부서를 설정
        List<String> subDeptCodes = new ArrayList<>(); // 중복을 제거하기 위한 리스트

        deptRelationsResponses.forEach(deptRelationsResponse -> {
            if (deptRelationsResponse.getSub_dept_code().equals(empDeptCode)) {
                // 현재 사원의 부서가 하위 부서인 경우, 상위 부서 설정
                employeeResponse.setPar_dept_code(deptRelationsResponse.getPar_dept_code());
            } else if (deptRelationsResponse.getPar_dept_code().equals(empDeptCode)) {
                // 현재 사원의 부서가 상위 부서인 경우, 하위 부서 추가
                String subDeptCode = deptRelationsResponse.getSub_dept_code();
                if (!subDeptCodes.contains(subDeptCode)) {
                    subDeptCodes.add(subDeptCode);
                }
            }
        });

        // List<String>을 쉼표로 구분된 하나의 문자열로 변환하여 설정
        String subDeptString = String.join(", ", subDeptCodes);
        employeeResponse.setSub_dept_code(subDeptString);
    }

    public OrgDetailResponse getOrgEmpDetail(int emp_code) {

        Employee employee = employeeRepository.findByEmpCode(emp_code);

        return OrgDetailResponse.getOrgDetail(employee);
    }

    public void resetEmpPass(int emp_code) {

        Employee employee = employeeRepository.findByEmpCode(emp_code);

        String encodePassword = passwordEncoder.encode(String.valueOf(emp_code));

        employee.resetPassword(encodePassword);

        employeeRepository.save(employee);
    }

    public void registApp(AappRegistGroupRequest aappRegistGroupRequest) {
        // 1. ApprovalAppoint 객체 생성 및 저장
        ApprovalAppoint approvalAppoint = new ApprovalAppoint(
                aappRegistGroupRequest.getAappCode(),
                aappRegistGroupRequest.getAappNo(),
                aappRegistGroupRequest.getAappDate(),
                aappRegistGroupRequest.getAappTitle()
        );
        approvalAppointRepository.save(approvalAppoint);

        // 2. 각 발령 상세 정보를 저장할 AppointDetail 객체들 생성 및 저장
        List<AappDetailRegistRequest> appDetails = aappRegistGroupRequest.getAppRegistDetails();
        List<AppointDetail> appointDetails = new ArrayList<>();

        for (AappDetailRegistRequest appDetail : appDetails) {
            Employee employee = employeeRepository.findByEmpCode(appDetail.getEmpCode());


            AppointDetail appointDetail = AppointDetail.of(
                    approvalAppoint,
                    appDetail.getAdetBefore(),
                    appDetail.getAdetAfter(),
                    appDetail.getAdetType(),
                    employee
            );

            appointDetails.add(appointDetail);
        }

        appointDetailRepository.saveAll(appointDetails);
    }

    public List<AappRegistListResponse> getAppRegistList() {
        return approvalAppointRepository.findAll().stream()
                .map(appointment -> new AappRegistListResponse(
                        appointment.getAappCode(),
                        appointment.getAappNo(),
                        appointment.getAappDate(),
                        appointment.getAappTitle()
                ))
                .collect(Collectors.toList());
    }

    public List<AappDetailRegistResponse> getAappDetailRegistList(String aappNo) {
        Optional<ApprovalAppoint> approvalAppointOptional = approvalAppointRepository.findById(aappNo);

        ApprovalAppoint approvalAppoint = approvalAppointOptional.get();
        String aappCode = approvalAppoint.getAappCode();

        List<AppointDetail> appointDetails = appointDetailRepository.findByApprovalAppoint_AappCode(aappCode);

        // 발령 정보 가져오기
        String aappNoResult = approvalAppoint.getAappNo();
        LocalDate aappDateResult = approvalAppoint.getAappDate();
        String aappTitleResult = approvalAppoint.getAappTitle();

        return appointDetails.stream()
                .map(ad -> {
                    Employee employee = ad.getEmployee();
                    String empName = (employee != null) ? employee.getEmp_name() : null;

                    return new AappDetailRegistResponse(
                            ad.getAdetCode(),
                            ad.getApprovalAppoint().getAappCode(),
                            aappNoResult,
                            aappDateResult,
                            aappTitleResult,
                            ad.getAdetBefore(),
                            ad.getAdetAfter(),
                            ad.getAdetType(),
                            empName
                    );
                })
                .collect(Collectors.toList());
    }

    public EmployeeListResponse getAllInfo() {

        List<Employee> allList = employeeRepository.findAll();

        return getEmployeeList(allList);
    }

    public void uploadProfileImg(Integer empCode, MultipartFile profileImg) throws IOException {
        if (!Files.exists(profileImgRoot)) {
            Files.createDirectories(profileImgRoot);
        }

        String originalFilename = profileImg.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = empCode + fileExtension;
        Path filePath = profileImgRoot.resolve(filename);
        Files.write(filePath, profileImg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Employee employee = employeeRepository.findById(empCode)
                .orElseThrow(() -> new RuntimeException("해당 사원 코드를 가진 사원을 찾을 수 없습니다: " + empCode));

        employee.profileImgRegist(filename); // 엔티티에 이미지 파일 이름 업데이트
        employeeRepository.save(employee);
    }

}
