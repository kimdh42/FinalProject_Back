package synergyhubback.employee.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.employee.domain.entity.Certificate;
import synergyhubback.employee.domain.entity.Title;

import java.util.List;


public interface TitleRepository extends JpaRepository<Title, String> {

    @Query("SELECT t FROM Title t WHERE t.title_code = :titleCode")
    Title findByTitleCode(String titleCode);

}
