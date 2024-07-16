package synergyhubback.common.address.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.employee.domain.entity.Department;

public interface AddressDeptRepository extends JpaRepository<Department, String> {
}
