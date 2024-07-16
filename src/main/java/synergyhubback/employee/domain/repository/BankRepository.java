package synergyhubback.employee.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Bank;
import synergyhubback.employee.domain.entity.Title;

import java.util.Optional;


public interface BankRepository extends JpaRepository<Bank, Integer> {

    @Query("SELECT b FROM Bank b WHERE b.bank_name = :bankName")
    Optional<Bank> findByName(String bankName);

}
