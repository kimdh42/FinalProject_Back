package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.approval.domain.entity.ApprovalAppoint;

import java.util.Optional;

public interface ApprovalAppointRepository extends JpaRepository<ApprovalAppoint, String> {

    @Query(value = "SELECT * FROM APPROVAL_APPOINT " +
            "ORDER BY SUBSTRING(AAPP_CODE, 1, 4), CAST(SUBSTRING(AAPP_CODE, 5) AS UNSIGNED) DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<ApprovalAppoint> findTopOrderByAappCodeDesc();

}
