package synergyhubback.attendance.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.attendance.domain.entity.Attendance;
import synergyhubback.attendance.domain.entity.AttendanceStatus;

public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Integer> {
}
