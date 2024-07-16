package synergyhubback.attendance.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.attendance.domain.entity.AttendanceStatus;
import synergyhubback.attendance.domain.entity.DayOffBalance;

import java.util.List;

public interface DayOffBalanceRepository extends JpaRepository<DayOffBalance, Integer> {
    DayOffBalance findTopByOrderByDbCodeDesc();

    @Query("SELECT a FROM DayOffBalance a WHERE a.employee.emp_code = :empCode")
    DayOffBalance findAllByEmpCode(int empCode);

    /* 연차촉진대상자 : 현월(1월이라면 1개)보다 적게 사용한 사원이 해당 */
    @Query("SELECT a FROM DayOffBalance a WHERE a.employee.emp_code = :empCode")
    DayOffBalance findRecipient(int empCode);
}
