package synergyhubback.attendance.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.attendance.domain.entity.DefaultSchedule;
import synergyhubback.attendance.domain.entity.OverWork;

public interface OverWorkRepository extends JpaRepository<OverWork, Integer> {
}
