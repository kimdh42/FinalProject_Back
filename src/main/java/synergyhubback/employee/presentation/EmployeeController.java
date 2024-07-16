package synergyhubback.employee.presentation;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import synergyhubback.attendance.presentation.ResponseMessage;
import synergyhubback.auth.util.TokenUtils;
import synergyhubback.common.exception.NotFoundException;
import synergyhubback.employee.dto.request.*;
import synergyhubback.employee.dto.response.*;
import synergyhubback.employee.service.EmployeeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final Path profileImgRoot = Paths.get("C:/SynergyHub/ProfileImg/");


    /* 로그아웃 시 토큰 무효화 */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {

        employeeService.updateRefreshToken(Integer.parseInt(userDetails.getUsername()), null);

        return ResponseEntity.ok().build();
    }

    /* 사원 등록 */
    @PostMapping("/empsRegist")
    public ResponseEntity<Void> empsRegist (@RequestBody @Valid EmpRegistDataRequest empRegistDataRequests, @RequestHeader("Authorization") String token) throws MessagingException {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<EmployeeRegistRequest> employeeRegistRequests = empRegistDataRequests.getEmployees();

        employeeService.empsRegist(
                employeeRegistRequests,
                empRegistDataRequests.getDetailErdNum(),
                empRegistDataRequests.getDetailErdTitle(),
                empCode,
                empRegistDataRequests.getDetailErdRegistdate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* 인사등록 리스트 조회 */
    @GetMapping("/empsResgistList")
    public ResponseEntity<List<DetailByEmpRegistResponseGroup>> getEmpRegistListGrouped() {

        List<DetailByEmpRegistResponseGroup> detailByEmpRegistResponseGroups = employeeService.getEmpRegistList();

        return ResponseEntity.ok(detailByEmpRegistResponseGroups);
    }

    /* 인사등록 리스트 상세조회 */
    @GetMapping("/empsRegistListDetail/{erd_num}")
    public ResponseEntity<EmpRegistDetailListResponse> getEmpsRegistListDetail(@PathVariable String erd_num) {

        EmpRegistDetailListResponse empRegistDetailListResponse = employeeService.getEmpsRegistListDetail(erd_num);

        return ResponseEntity.ok(empRegistDetailListResponse);
    }

    /* 내 정보 조회 */
    @GetMapping("/myInfo")
    public ResponseEntity<MyInfoResponse> getMyInfo(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        MyInfoResponse myInfoResponse = employeeService.getMyInfo(empCode);

        return ResponseEntity.ok(myInfoResponse);
    }

    /* 내 정보 수정 */
    @PatchMapping("/updateMyInfo")
    public ResponseEntity<EmployeeResponse> updateMyInfo(@RequestHeader("Authorization") String token,
                                                         @RequestBody(required = false) EmployeeUpdateRequest employeeUpdateRequest) {
        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        EmployeeResponse employeeResponse = employeeService.updateMyInfo(empCode, employeeUpdateRequest);

        return ResponseEntity.ok(employeeResponse);
    }

    /* 사원 전체 조회 */
    @GetMapping("/employeeAll")
    public ResponseEntity<List<EmployeeResponse>> getEmployeeAll() {

        List<EmployeeResponse> employeeResponses  = employeeService.getEmployeeAll();

        return ResponseEntity.ok(employeeResponses );
    }

    /* 인사기록카드 조회 하고 싶어요 */
    @GetMapping("/recordCard")
    public ResponseEntity<RecordCardResponse> getRecordCard(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        RecordCardResponse recordCardResponse = employeeService.getRecordCard(empCode);

        return ResponseEntity.ok(recordCardResponse);
    }

    /* 인사기록카드 등록 */
    @PostMapping("/registRecordCard")
    public ResponseEntity<Void> registRecordCard(@RequestHeader("Authorization") String token,
                                                 @RequestBody @Valid RegistRecordCardRequest registRecordCardRequest) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        System.out.println("Received emp_code from request: " + registRecordCardRequest.getEmp_code());
        System.out.println("Received schoolInfos from request: " + registRecordCardRequest.getSchoolInfos());

        // 직원 코드를 요청 객체에 추가
        registRecordCardRequest.setEmp_code(empCode);

        // 서비스 메서드 호출
        employeeService.registRecordCard(registRecordCardRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* 인사기록카드 수정 */
    @PatchMapping("/updateRecordCard")
    public ResponseEntity<Void> updateRecordCard(@RequestHeader("Authorization") String token,
                                                 @RequestBody @Valid RegistRecordCardRequest registRecordCardRequest) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        registRecordCardRequest.setEmp_code(empCode);

        employeeService.updateRecordCard(registRecordCardRequest);

        return ResponseEntity.ok().build();
    }

    /* 팀원 인사기록 카드 조회 */
    @GetMapping("/teamRecordCard/{emp_code}")
    public ResponseEntity<RecordCardResponse> getTeamRecordCard(@RequestHeader("Authorization") String token, @PathVariable int emp_code) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int logInEmpCode = Integer.parseInt(tokenEmpCode);


        try {
            RecordCardResponse recordCardResponse = employeeService.getTeamRecordCard(logInEmpCode, emp_code);
            return ResponseEntity.ok(recordCardResponse);
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /* 팀원 정보 조회 */
    @GetMapping("/employeeList")
    public ResponseEntity<EmployeeListResponse> employeeList(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        System.out.println("token : " + token);
        System.out.println("empCode : " + empCode);

        EmployeeListResponse employeeListResponse = employeeService.employeeList(empCode);

        return ResponseEntity.ok(employeeListResponse);
    }

    /* 부서 등록 */
    @PostMapping("/registDept")
    public ResponseEntity<Void> registDept(@RequestBody @Valid RegistDeptRequest registDeptRequest) {

        employeeService.registDept(registDeptRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* 부서 상위 하위 관계 등록 */
    @PostMapping("/registDeptRelations")
    public ResponseEntity<Void> registDeptRelations(@RequestBody RegistDeptRelationsRequest registDeptRelationsRequest) {

        employeeService.registDeptRelations(registDeptRelationsRequest);

        return ResponseEntity.ok().build();
    }

    /* 부서 상위 하위 관계 수정 */
    @PutMapping("/modifyDeptRelations/{dept_relations_code}")
    public ResponseEntity<Void> modifyDeptRelations(@PathVariable int dept_relations_code, @RequestBody ModifyDeptRelationsRequest modifyRequest) {

        employeeService.modifyDeptRelations(
                dept_relations_code,
                modifyRequest.getParentDepartment(),
                modifyRequest.getSubDepartment());

        return ResponseEntity.ok().build();
    }

    /* 부서 상위 하위 관계 삭제 */
    @DeleteMapping("/deleteDeptRelations")
    public ResponseEntity<Void> deleteDeptRelations(@RequestBody RegistDeptRelationsRequest registDeptRelationsRequest) {

        employeeService.deleteDeptRelations(registDeptRelationsRequest);

        return ResponseEntity.ok().build();
    }


    /* 부서 전체 조회(상위,하위 부서 관계 포함) */
    @GetMapping("/departments")
    public ResponseEntity<List<GetDeptTitleResponse>> getDepartments() {

        List<GetDeptTitleResponse> getDeptTitleResponse = employeeService.getDepartments();

        return ResponseEntity.ok(getDeptTitleResponse);
    }

    /* 부서 상세 조회 */
    @GetMapping("/deptDetailList/{dept_code}")
    public ResponseEntity<DepartmentResponse> getDepartmentList(@PathVariable String dept_code) {

        DepartmentResponse departmentResponse = employeeService.getDeptDetailList(dept_code);

        return ResponseEntity.ok(departmentResponse);
    }


    /* 직위 전체 조회 */
    @GetMapping("/empTitles")
    public ResponseEntity<List<EmpTitleListResponse>> getEmpTitleList() {

        List<EmpTitleListResponse> empTitleList = employeeService.getEmpTitleList();

        return ResponseEntity.ok(empTitleList);
    }

    /* 직급 전체 조회 */
    @GetMapping("/empPositions")
    public ResponseEntity<List<GetPositionNameResponse>> getPositionList() {

        List<GetPositionNameResponse> empPositionList = employeeService.getPositionList();

        return ResponseEntity.ok(empPositionList);
    }

    /* 조직도 조회 */
    @GetMapping("/org")
    public ResponseEntity<List<EmployeeResponse>> getOrg() {

        List<EmployeeResponse> employeeResponseList = employeeService.getOrg();

        return ResponseEntity.ok(employeeResponseList);
    }

    /* 조직도 상세 조회 */
    @GetMapping("/orgDetail/{emp_code}")
    public ResponseEntity<OrgDetailResponse> getOrgDetail(@PathVariable int emp_code) {

        OrgDetailResponse orgDetailResponse = employeeService.getOrgEmpDetail(emp_code);

        return ResponseEntity.ok(orgDetailResponse);
    }

    /* 비밀번호 초기화 */
    @PutMapping("/resetEmpPass/{emp_code}")
    public ResponseEntity<ResetEmpPassRequest> patchEmpPass(@PathVariable int emp_code, @RequestHeader("Authorization") String token) {

        employeeService.resetEmpPass(emp_code);

        return ResponseEntity.ok().build();
    }

    /* 발령 등록 */
    @PostMapping("/appRegist")
    public ResponseEntity<Void> registApp(@RequestBody @Valid AappRegistGroupRequest aappRegistGroupRequest) {

        employeeService.registApp(aappRegistGroupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* 발령등록 리스트 조회 */
    @GetMapping("/appRegistList")
    public ResponseEntity<List<AappRegistListResponse>> getAppRegistList() {

        List<AappRegistListResponse> appRegistList = employeeService.getAppRegistList();

        return ResponseEntity.ok(appRegistList);
    }

    /* 발령등록 리스트 상세 조회 */
    @GetMapping("/appRegistListDetail/{aappNo}")
    public ResponseEntity<List<AappDetailRegistResponse>> getAappRegistListDetail(@PathVariable String aappNo) {

        List<AappDetailRegistResponse> aappDetailRegistList = employeeService.getAappDetailRegistList(aappNo);

        return ResponseEntity.ok(aappDetailRegistList);
    }

      /* 모든 사원 정보 조회 */
    @GetMapping("/all")
    public ResponseEntity<EmployeeListResponse> getAllInfo() {

        EmployeeListResponse allInfo = employeeService.getAllInfo();

        return ResponseEntity.ok(allInfo);
    }

    /* 프로필 이미지 업로드 */
    @PostMapping("/uploadProfileImg")
    public ResponseEntity<Void> uploadProfileImg(@RequestParam("empCode") int empCode, @RequestParam("profileImg") MultipartFile profileImg) {
        try {
            System.out.println("Received empCode: " + empCode);  // 디버깅을 위한 출력
            System.out.println("Received file: " + profileImg.getOriginalFilename());
            employeeService.uploadProfileImg(empCode, profileImg);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /* 프로필 이미지 검색 */
    @GetMapping("/profileImg")
    public ResponseEntity<Resource> getProfileImg(@RequestParam int empCode) {
        try {
            Path filePath = profileImgRoot.resolve(empCode + "profile.png");
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath));
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PatchMapping("/resetEmpPass/{emp_code}")
//    public ResponseEntity<ResetEmpPassRequest> patchEmpPass(@PathVariable int emp_code, @RequestHeader("Authorization") String token) {
//
//        String jwtToken = TokenUtils.getToken(token);
//        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
//        int empCode = Integer.parseInt(tokenEmpCode);
//
//        String tokenDeptCode = TokenUtils.getDeptCode(jwtToken);
//
//        List<String> allowedDeptCodes = List.of("D4", "D5", "D6");      // 인사부서(인사부, 채용팀, 교육개발팀) 인원만 비밀번호 초기화 가능
//                                                                          // 팀 인원이 초기화 해주는걸로 바꿈
//        System.out.println("allowedDeptCodes : " + allowedDeptCodes);
//        System.out.println("tokenDeptCode : " + tokenDeptCode);
//
//        if(tokenDeptCode == null || !allowedDeptCodes.contains(tokenDeptCode)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        employeeService.resetEmpPass(emp_code);
//
//        return ResponseEntity.ok().build();
//    }
}

