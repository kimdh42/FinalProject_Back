package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.approval.domain.entity.Line;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Integer> {
    List<Line> findByLineSortLsCodeOrderByAlOrderAsc(int lsCode);
}
