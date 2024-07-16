package synergyhubback.pheed.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.pheed.domain.entity.Pheed;

import java.util.List;

public interface PheedRepository extends JpaRepository<Pheed, Integer> {


    @Query("SELECT p FROM Pheed p WHERE p.employee.emp_code = :empCode AND p.deStatus = 'N' ORDER BY p.creStatus DESC")
    List<Pheed> findByEmployeeEmpCode(@Param("empCode") int empCode);

    @Query("SELECT p FROM Pheed p WHERE p.employee.emp_code = :empCode")
    List<Pheed> findAllByEmpCode(@Param("empCode") int empCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pheed p WHERE p.pheedSort = :adCode")
    int deleteByPheedSort(String adCode);

    @Query("SELECT p FROM Pheed p WHERE p.pheedSort = :test")
    Pheed findByPheedSort(String test);

    @Query("SELECT p FROM Pheed p WHERE p.pheedCode = :pheedCode")
    Pheed findByPheedCode(int pheedCode);
}