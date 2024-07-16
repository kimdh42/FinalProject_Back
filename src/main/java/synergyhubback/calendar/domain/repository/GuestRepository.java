package synergyhubback.calendar.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.calendar.domain.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, String> {
    @Query(value = "SELECT * FROM GUEST ORDER BY CAST(SUBSTRING(GUEST_CODE, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    Optional<Guest> findTopByOrderByGuestCodeDesc();

    @Modifying
    @Query("DELETE FROM Guest g WHERE g.task.id = :taskId")
    void deleteByTaskId(@Param("taskId") String taskId);

    @Modifying
    @Query("DELETE FROM Guest g WHERE g.event.id = :eventId")
    void deleteByEventId(@Param("eventId") String eventId);

    @Query("SELECT g FROM Guest g WHERE g.event.id = :eventId")
    List<Guest> findByEventId(@Param("eventId") String eventId);

    @Query("SELECT g FROM Guest g WHERE g.task.id = :taskId")
    List<Guest> findByTaskId(@Param("taskId") String taskId);


}
