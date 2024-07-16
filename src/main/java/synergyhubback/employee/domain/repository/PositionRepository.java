package synergyhubback.employee.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Position;

public interface PositionRepository extends JpaRepository<Position, String> {

    @Query("SELECT p FROM Position p WHERE p.position_code = :positionCode")
    Position findByPositionCode(String positionCode);

}
