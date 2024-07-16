package synergyhubback.message.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Employee;

import java.util.Optional;

public interface MsgEmpRepository extends JpaRepository<Employee, Integer> {
}
