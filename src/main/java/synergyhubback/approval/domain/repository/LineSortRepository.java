package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.approval.domain.entity.LineSort;

public interface LineSortRepository extends JpaRepository<LineSort, Integer> {
}
