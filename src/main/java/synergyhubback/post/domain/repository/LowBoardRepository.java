package synergyhubback.post.domain.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import synergyhubback.post.domain.entity.LowBoardEntity;
import synergyhubback.post.dto.response.LowBoardResponse;

import java.util.List;

@Repository
public interface LowBoardRepository extends JpaRepository<LowBoardEntity, Long> {


    @Transactional
    @Modifying
    @Query("UPDATE LowBoardEntity lb " +
            "SET lb.LowBoardName = :lowName " +
            "WHERE lb.LowBoardCode = :lowCode")
    int boardUpdate(@Param("lowName") String lowName, @Param("lowCode") int lowCode);



    @Transactional
    @Modifying
    @Query("update LowBoardEntity lb set lb.LowBoardName = 'Deleted' where lb.LowBoardCode = :lowCode")
    Integer boardDelete(int lowCode);


    @Query("select lb.LowBoardCode from LowBoardEntity lb ORDER BY lb.LowBoardCode DESC LIMIT 1")
    int lastLowBoard();

    @Query("select lb from LowBoardEntity lb where lb.LowBoardCode = :lowBoardCode")
    LowBoardEntity callGETLowBoardListToCode(String lowBoardCode);
}
