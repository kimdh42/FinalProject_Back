package synergyhubback.calendar.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import synergyhubback.calendar.domain.entity.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    // 필요한 추가적인 쿼리 메서드를 여기에 작성할 수 있습니다.
    @Query(value = "SELECT * FROM TASK ORDER BY CAST(SUBSTRING(TASK_CODE, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    Optional<Task> findTopByOrderByIdDesc();

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN t.guests g WHERE t.employee.emp_code = :empCode OR g.employee.emp_code = :empCode")
    List<Task> findTasksByEmployeeOrGuest(@Param("empCode") int empCode);

    /* 박은비 추가 */
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN t.guests g WHERE t.employee.emp_code = :empCode OR g.employee.emp_code = :empCode AND t.status = 'B'")
    List<Task> findTasksByStatus(int empCode);
}
