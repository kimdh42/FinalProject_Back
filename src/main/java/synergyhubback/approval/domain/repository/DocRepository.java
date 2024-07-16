package synergyhubback.approval.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.approval.domain.entity.ApprovalAttendance;
import synergyhubback.approval.domain.entity.Document;
import synergyhubback.approval.dto.response.DocListResponse;
import synergyhubback.approval.dto.response.DocumentResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DocRepository extends JpaRepository<Document, String> {

    Document findByAdDetail(String code);

    @Query(value = "SELECT * FROM APPROVAL_DOC " +
            "ORDER BY SUBSTRING(AD_CODE, 1, 2), CAST(SUBSTRING(AD_CODE, 3) AS UNSIGNED) DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Document> findTopOrderByAdCodeDesc();

    @Query("SELECT d.adDetail FROM Document d WHERE d.adCode = :adCode")
    String findAdDetailById(String adCode);

    @Query("SELECT d.adTitle FROM Document d WHERE d.adCode = :adCode")
    String findAdTitleById(String adCode);

    @Query("SELECT d.employee.emp_code FROM Document d WHERE d.adCode = :adCode")
    int findEmployeeEmpCodeById(String adCode);

    @Query("SELECT d.adStatus FROM Document d WHERE d.adCode = :adCode")
    String findAdStatusById(String adCode);

    Boolean existsByForm_AfCode(int afCode);

    /* 휴가, 휴직 기록을 위한 조회 : 박은비 */
    @Query("SELECT d FROM Document d WHERE d.form.afCode = 7 AND d.adStatus = '완료' AND d.employee.emp_code = :empCode")
    List<Document> findAfCodeAndAdStatusAndEmpCode(@Param("empCode") int empCode);

    /* 예외근무 신청 현황 (신청일자에 따른 정렬, 최대 5개) */
    @Query("SELECT d FROM Document d WHERE d.form.afCode = 2 AND d.employee.emp_code = :empCode ORDER BY d.adReportDate DESC")
    List<DocumentResponse> findCurrentBTList(int empCode);

    /* 초과근무 신청 현황 (신청일자에 따른 정렬, 최대 5개) */
    @Query("SELECT d FROM Document d WHERE d.form.afCode = 3 AND d.employee.emp_code = :empCode ORDER BY d.adReportDate DESC")
    List<DocumentResponse> findCurrentOWList(int empCode);

    /* 휴가 신청 현황 (신청일자에 따른 정렬, 최대 5개) */
    @Query("SELECT d FROM Document d WHERE d.form.afCode = 5 AND d.employee.emp_code = :empCode ORDER BY d.adReportDate DESC")
    List<DocumentResponse> findCurrentDOList(int empCode);

    /* 월간 결재 휴가 승인 현황 */
    @Query("SELECT d FROM Document d WHERE d.employee.emp_code = :empCode AND d.adStatus = '완료' ORDER BY d.adReportDate DESC")
    List<DocumentResponse> findByEmpCode(int empCode);




}
