package synergyhubback.approval.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.approval.domain.entity.Personal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonalRepository extends JpaRepository<Personal, String> {
    @Query(value = "SELECT * FROM APPROVAL_PERSONAL " +
            "ORDER BY SUBSTRING(AP_CODE, 1, 2), CAST(SUBSTRING(AP_CODE, 3) AS UNSIGNED) DESC " +
            "LIMIT 1", nativeQuery = true)

    Optional<Personal> findTopOrderByApCodeDesc();

    /* 휴직 조회 로직 */
    @Query("SELECT p FROM Personal p WHERE :currentDate >= p.apStart AND :currentDate <= p.apEnd")
    Personal findByApStartAndApEnd(@Param("currentDate") LocalDate currentDate);

}
