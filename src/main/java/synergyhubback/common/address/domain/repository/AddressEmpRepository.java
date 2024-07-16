package synergyhubback.common.address.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Employee;

import java.util.List;


public interface AddressEmpRepository extends JpaRepository<Employee, Integer> {

    @Query("SELECT e FROM Employee e WHERE e.emp_code != :empCode AND e.emp_code NOT IN (SELECT mb.blkName.emp_code FROM MessageBlock mb WHERE mb.blkId.emp_code = :empCode)")
    List<Employee> findAddressNotBlkEmp(int empCode);
}
