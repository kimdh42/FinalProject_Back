package synergyhubback.employee.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.entity.SchoolInfo;
import synergyhubback.employee.dto.response.SchoolInfoResponse;

import java.util.List;


public interface SchoolInfoRepository extends JpaRepository<SchoolInfo, Integer> {

    /* 사원코드로 학교정보 조회 */
    @Query("SELECT s FROM SchoolInfo s WHERE s.emp_code = :empCode")
    List<SchoolInfo> findAllByEmpCode(int empCode);

}
