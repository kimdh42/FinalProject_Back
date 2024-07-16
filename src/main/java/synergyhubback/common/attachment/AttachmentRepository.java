package synergyhubback.common.attachment;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {

    @Query("SELECT a FROM AttachmentEntity a WHERE a.attachSort = :postCode")
    List<AttachmentEntity> getFile(@Param("postCode") String postCode);

    List<AttachmentEntity> findByAttachSort(String adCode);

    void deleteByAttachSort(String adCode);

    @Query("SELECT a FROM AttachmentEntity a WHERE a.attachSort = :msgCode")
    List<AttachmentEntity> findByMsgCode(String msgCode);
}
