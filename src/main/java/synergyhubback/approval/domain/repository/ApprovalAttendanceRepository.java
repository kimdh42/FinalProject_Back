package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.approval.domain.entity.ApprovalAttendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApprovalAttendanceRepository extends JpaRepository<ApprovalAttendance, String> {

    @Query(value = "SELECT * FROM APPROVAL_ATTENDANCE " +
            "ORDER BY SUBSTRING(AATT_CODE, 1, 4), CAST(SUBSTRING(AATT_CODE, 5) AS UNSIGNED) DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<ApprovalAttendance> findTopOrderByAattCodeDesc();

    /* 휴가 기록 조회 */
    @Query("SELECT a FROM ApprovalAttendance a WHERE :currentDate >= a.aattStart AND :currentDate <= a.aattEnd")
    ApprovalAttendance findByAttStartAndAttEnd(LocalDateTime currentDate);

    /* AATT 코드로 조회 */
    @Query("SELECT a FROM ApprovalAttendance a WHERE a.aattCode = :aattCode")
    ApprovalAttendance findByAATTCode(String aattCode);

    /* 이번달 휴가신청서 조회 */
    @Query("SELECT a FROM ApprovalAttendance a WHERE a.aattStart >= :startDate AND a.aattEnd <= :endDate")
    List<ApprovalAttendance> findByCurrentMonth(LocalDateTime startDate, LocalDateTime endDate);
}
