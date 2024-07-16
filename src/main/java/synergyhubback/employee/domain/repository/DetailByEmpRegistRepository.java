package synergyhubback.employee.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.DetailByEmpRegist;

import java.util.List;


public interface DetailByEmpRegistRepository extends JpaRepository<DetailByEmpRegist, Integer> {

    /* 모든 인사내역 조회 */
    List<DetailByEmpRegist> findAll();

    /* 인사내역 상세 조회 */
    @Query("SELECT d FROM DetailByEmpRegist d WHERE d.erd_num = :erdNum")
    List<DetailByEmpRegist> findEmpRegistListDetail(String erdNum);
}
