package synergyhubback.message.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import synergyhubback.message.domain.entity.MessageBlock;
import synergyhubback.message.dto.request.CreateBlockEmpRequest;

public interface MessageBlockRepository extends JpaRepository<MessageBlock, Integer> {

    @Query("SELECT MAX(b.blkCode) AS LastInsertedCode FROM MessageBlock b")
    Integer findLastBlkCode();

    @Query("SELECT b FROM MessageBlock b WHERE b.blkId.emp_code = :blkId AND b.blkName.emp_code = :blkName")
    MessageBlock findByBlkIdAndBlkName(int blkId, int blkName);
}
