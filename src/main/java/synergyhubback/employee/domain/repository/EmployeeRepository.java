package synergyhubback.employee.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.Employee;
import java.util.List;
import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Integer> {


    // 이재현 로그인 관련 repository 로직 생성
    Optional<Employee> findByRefreshToken(String refreshToken);


    /* 입사년월로 사원코드 생성 */
    @Query("SELECT COUNT(e) FROM Employee e WHERE FUNCTION('DATE_FORMAT', e.hire_date, '%Y%m') = :hireYearMonth")
    long countByHireYearMonth(String hireYearMonth);


    /* 아이디로 내정보 조회, 인사기록카드 조회 */
    @Query("SELECT e FROM Employee e WHERE e.emp_code = :empCode")
    Employee findByEmpCode(int empCode);

    /* 팀원 정보 조회 */
    @Query("SELECT e FROM Employee e WHERE e.department.dept_code = :deptCode AND e.emp_status = 'Y'")
    List<Employee> findAllByDeptCode(@Param("deptCode") String deptCode);

    /* 사원코드로 부서코드 조회 */
    @Query("SELECT e.department.dept_code FROM Employee e WHERE e.emp_code = :empCode")
    String findDeptCodeByEmpCode(@Param("empCode") int empCode);

    @Query("SELECT e.emp_name FROM Employee e JOIN e.title t WHERE e.department.dept_code = :deptCode AND t.title_code = 'T4'")
    default String findManagerByDeptCode(String deptCode) {
        List<String> results = findManagersByDeptCode(deptCode);
        return results.isEmpty() ? null : results.get(0);
    }

    @Query("SELECT e.emp_name FROM Employee e JOIN e.title t WHERE e.department.dept_code = :deptCode AND t.title_code = 'T4' AND e.emp_status = 'Y'")
    List<String> findManagersByDeptCode(String deptCode);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.dept_code = :deptCode AND e.emp_status = 'Y'")
    int countTeamMembersByDeptCode(String deptCode);

    @Query("SELECT e.emp_name FROM Employee e WHERE e.department.dept_code = :deptCode")
    List<String> findTeamMemberNamesByDeptCode(String deptCode);

    /* 사원코드로 이름 조회 : 이다정 */
    @Query("SELECT e.emp_name FROM Employee e WHERE e.emp_code = :empCode")
    String findEmpNameById(int empCode);


    @Query("SELECT e FROM Employee e WHERE e.emp_status = 'Y'")
    List<Employee> findAllActiveEmployees();

    List<Employee> findByDepartment(Department department);

    /* --- 박은비 --- */

    // D2 하위 사원 조회
    @Query("SELECT e FROM Employee e WHERE e.department.dept_code IN ('d2', 'd7', 'd8', 'd9', 'd10', 'd11', 'd12')")
    List<Employee> findByD2();

    // D3 하위 사원 조회
    @Query("SELECT e FROM Employee e WHERE e.department.dept_code IN ('d3', 'd4', 'd5', 'd6', 'd13', 'd14', 'd15')")
    List<Employee> findByD3();

    // D7 하위 사원 조회
    @Query("SELECT e FROM Employee e WHERE e.department.dept_code IN ('d7', 'd8', 'd9')")
    List<Employee> findByD7();

    // D10 하위 사원 조회
    @Query("SELECT e FROM Employee e WHERE e.department.dept_code IN ('d10', 'd11', 'd12')")
    List<Employee> findByD10();

    // D4 하위 사원 조회
    @Query("SELECT e FROM Employee e WHERE e.department.dept_code IN ('d4', 'd5', 'd6')")
    List<Employee> findByD4();

    // D13 하위 사원 조회
    @Query("SELECT e FROM Employee e WHERE e.department.dept_code IN ('d13', 'd14', 'd15')")
    List<Employee> findByD13();

}
