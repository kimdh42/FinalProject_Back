package synergyhubback.attendance.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.attendance.domain.entity.AttendanceStatus;
import synergyhubback.attendance.domain.entity.DayOff;
import synergyhubback.attendance.domain.entity.DayOffBalance;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.util.List;

public interface DayOffRepository extends JpaRepository<DayOff, Integer> {

    @Query("SELECT a FROM DayOff a WHERE a.employee.emp_code = :empCode")
    List<DayOff> findAllByEmpCode(int empCode);

    @Query("SELECT a FROM DayOff a WHERE a.employee.emp_code = :empCode AND a.doStartDate = :doStartDate")
    DayOff findByEmpCode(int empCode, String doStartDate);

    @Query("SELECT a FROM DayOffBalance a WHERE a.employee.emp_code = :empCode")
    DayOffBalance findMyDayOffBalanceByEmpCode(int empCode);

    @Query("SELECT a FROM DayOff a WHERE a.doStartDate = :doStartDate OR a.doEndDate = :doEndDate")
    List<DayOff> findDoByStartDateOrDoEndDate(LocalDate doStartDate, LocalDate doEndDate);

    /* 사원 코드 일치 및 시작 날짜와 종료 날짜에 해당되는 날짜인지 조회 */
    @Query("SELECT d FROM DayOff d " +
            "WHERE d.employee = :empCode " +
            "AND :currentDate >= d.doStartDate and :currentDate <= d.doEndDate")
    DayOff findByEmpCodeAndCurrentDate(@Param("empCode") Employee empCode, @Param("currentDate") LocalDate currentDate);
}
