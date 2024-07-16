package synergyhubback.employee.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Certificate;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.dto.response.CertificateResponse;

import java.util.List;


public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    /* 사원코드로 자격증 정보 조회 */
    @Query("SELECT c FROM Certificate c WHERE c.emp_code = :empCode")
    List<Certificate> findAllByEmpCode(int empCode);

}
