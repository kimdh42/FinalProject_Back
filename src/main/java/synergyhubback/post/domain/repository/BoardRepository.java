package synergyhubback.post.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import synergyhubback.post.domain.entity.BoardEntity;
@Repository
public interface BoardRepository extends JpaRepository<BoardEntity , Integer> {

    @Query("select b from BoardEntity b where b.BoardCode = :boardCode")
    BoardEntity findByBoardCode(int boardCode);
}
