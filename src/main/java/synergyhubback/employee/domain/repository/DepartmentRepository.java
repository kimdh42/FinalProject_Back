package synergyhubback.employee.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Certificate;
import synergyhubback.employee.domain.entity.Department;

import java.util.List;
import java.util.Optional;


public interface DepartmentRepository extends JpaRepository<Department, String> {

    @Query("SELECT d FROM Department d WHERE d.dept_code = :deptCode")
    Department findByDeptCode(@Param("deptCode") String deptCode);

    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.subDepartments cd LEFT JOIN FETCH d.parentDepartments pd WHERE d.end_date IS NULL")
    List<Department> findAllAndRelations();

    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.subDepartments cd LEFT JOIN FETCH d.parentDepartments pd WHERE d.dept_code = :deptCode AND d.end_date IS NULL")
    Department findDeptDetailByDeptCode(@Param("deptCode") String deptCode);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.subDepartments WHERE d.dept_code = :deptCode")
    Optional<Department> findByDeptCodeWithSubDepartments(String deptCode);

}
