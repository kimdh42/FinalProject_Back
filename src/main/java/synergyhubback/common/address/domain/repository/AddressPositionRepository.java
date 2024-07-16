package synergyhubback.common.address.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.employee.domain.entity.Position;

public interface AddressPositionRepository extends JpaRepository<Position, String> {
}
