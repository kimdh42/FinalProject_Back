package synergyhubback.calendar.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synergyhubback.calendar.domain.entity.Label;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
}

