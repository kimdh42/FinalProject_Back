package synergyhubback.attendance.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import synergyhubback.attendance.domain.entity.DefaultSchedule;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DefaultScheduleRepository extends JpaRepository<DefaultSchedule, Integer> {

    /* 모든 파라미터를 통한 지정 출퇴근시간 조회 */
    List<DefaultSchedule> findByDeptTitleAndAtdStartTimeAndAtdEndTime(String deptTitle, LocalTime atdStartTime, LocalTime atdEndTime);

    /* 부서이름을 통한 지정 출퇴근시간 조회 */
    List<DefaultSchedule> findAllByDeptTitle(String deptTitle);

    /* 부서이름과 사원코드를 통한 지정 출퇴근시간 조회 */
    DefaultSchedule findByDeptTitleAndEmployee(String deptTitle, Employee employee);

    /* 사원정보를 통한 지정 출퇴근시간 조회 */
    DefaultSchedule findByEmployee(Employee employee);

    /* 사원정보가 없고, 팀이 있는 지정 출퇴근시간 조회 */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.employee.department.dept_title = :deptCode AND ds.employee IS NULL")
    DefaultSchedule findWithDeptTitleAndNullEmployee(@Param("deptTitle") String deptCode);

    /**/

    DefaultSchedule findByParTitle(String deptCode);

    /* 하위부서를 통해 지정 출퇴근시간 조회 (사원정보 없음) */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.subTitle = :subTitle AND ds.employee IS NULL")
    DefaultSchedule findBySubTitle(String subTitle);

    /* 팀을 통해 지정 출퇴근시간 조회 (사원정보 없음) */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.deptTitle = :deptTitle AND ds.employee IS NULL")
    DefaultSchedule findByDeptTitle(String deptTitle);

    /* 사원정보가 없으나 오늘 날짜인 모든 항목 조회 */
    List<DefaultSchedule> findByEmployeeIsNull();

    /* 사원정보가 없으나 오늘 날짜인 모든 항목 조회 */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.employee IS NULL AND :currentDate BETWEEN ds.dsStartDate AND ds.dsEndDate")
    List<DefaultSchedule> findByEmployeeIsNullAndDate(@Param("currentDate") LocalDate currentDate);

    /* 지정한 날짜에 오늘 날짜가 포함되는지 조회 */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE :currentDate BETWEEN ds.dsStartDate AND ds.dsEndDate")
    List<DefaultSchedule> findByDate(@Param("currentDate") LocalDate currentDate);

    /* 오늘 날짜와 사원번호로 조회 */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.employee = :employee AND :currentDate BETWEEN ds.dsStartDate AND ds.dsEndDate")
    DefaultSchedule findByEmployeeAndDate(@Param("employee") Employee employee, @Param("currentDate") LocalDate currentDate);

    /* 날짜가 없고 사원번호가 존재하는 기록 조회 */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.employee = :employee AND ds.dsStartDate IS NULL")
    DefaultSchedule findByEmployeeAndDateIsNull(Employee employee);

    /* 날짜가 없는 모든 기록 조회 */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.dsStartDate IS NULL")
    List<DefaultSchedule> findByDateIsNull();

    /* 사원번호와 날짜가 없는 모든 기록 조회 */
    @Query("SELECT ds FROM DefaultSchedule ds WHERE ds.employee IS NULL AND ds.dsStartDate IS NULL")
    List<DefaultSchedule> findByEmployeeIsNullAndDateIsNull();
}
