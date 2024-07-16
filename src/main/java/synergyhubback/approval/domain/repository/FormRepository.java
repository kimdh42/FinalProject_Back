package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.approval.domain.entity.Form;

public interface FormRepository extends JpaRepository<Form, Integer> {
}
