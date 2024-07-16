package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.approval.domain.entity.Etc;

import java.util.Optional;

public interface EtcRepository extends JpaRepository<Etc, String> {
    @Query(value = "SELECT * FROM APPROVAL_ETC " +
            "ORDER BY SUBSTRING(AE_CODE, 1, 2), CAST(SUBSTRING(AE_CODE, 3) AS UNSIGNED) DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Etc> findTopOrderByAeCodeDesc();
}
