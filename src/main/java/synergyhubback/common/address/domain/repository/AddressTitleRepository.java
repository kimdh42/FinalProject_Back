package synergyhubback.common.address.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.employee.domain.entity.Title;

public interface AddressTitleRepository extends JpaRepository<Title, String> {
}
