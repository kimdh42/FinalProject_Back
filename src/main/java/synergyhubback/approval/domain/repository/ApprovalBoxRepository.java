package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.approval.domain.entity.ApprovalBox;

import java.util.List;

public interface ApprovalBoxRepository extends JpaRepository<ApprovalBox, Integer> {
    List<ApprovalBox> findByEmpCode(Integer empCode);
}
