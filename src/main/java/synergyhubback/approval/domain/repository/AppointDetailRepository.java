package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synergyhubback.approval.domain.entity.AppointDetail;
import synergyhubback.approval.domain.entity.ApprovalAppoint;

import java.util.List;

public interface AppointDetailRepository extends JpaRepository<AppointDetail, Integer> {

    @Query("SELECT ad FROM AppointDetail ad " +
            "JOIN ad.approvalAppoint aa " +
            "JOIN ad.employee e " +
            "WHERE ad.approvalAppoint.aappCode = :adDetail")
    List<AppointDetail> findByAdDetail(String adDetail);

    List<AppointDetail> findByApprovalAppoint_AappCode(String adDetail);

}
