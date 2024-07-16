package synergyhubback.post.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import synergyhubback.post.domain.entity.*;
import synergyhubback.post.domain.type.PostCommSet;
import synergyhubback.post.dto.request.CommontRequest;
import synergyhubback.post.dto.request.PostRequest;
import synergyhubback.post.dto.request.PostRoleRequest;
import synergyhubback.post.dto.response.CommonResponse;
import synergyhubback.post.dto.response.PostResponse;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT p FROM PostEntity p ORDER BY p.PostCode DESC LIMIT 1")
    PostEntity LastPost();

    @Query("SELECT p FROM LowBoardEntity p WHERE p.BoardCode.BoardCode = :boardCode")
    List<LowBoardEntity> getLowBoard(@Param("boardCode") Integer boardCode);

    @Query("select b from BoardEntity b")
    List<BoardEntity> getAllBoard();

    @Query("select p from PostSortEntity p ")
    List<PostSortEntity> getAllPostSortList();

    @Query("select p from PostEntity p where p.LowBoardCode.LowBoardCode = :lowCode AND p.FixStatus <> 'D'")
    List<PostResponse> InboardList(Pageable pageable,@Param("lowCode") Integer lowCode);

    @Query("SELECT p FROM PostEntity p WHERE p.LowBoardCode.LowBoardCode = :lowCode AND p.FixStatus <> 'D' ORDER BY p.PostCode DESC")
    List<PostEntity> findNotice(@Param("lowCode") Integer lowCode);

    @Query("SELECT p FROM PostEntity p WHERE p.FixStatus = 'Y' AND p.LowBoardCode.LowBoardCode = :lowCode AND p.FixStatus <> 'D' ORDER BY p.PostCode DESC")
    List<PostResponse> InboardPinList(Pageable pageable,@Param("lowCode") Integer lowCode);

    @Query("SELECT new synergyhubback.post.dto.response.PostResponse(p.PostCode, p.EmpCode.emp_code, p.PostName, p.PostDate, p.PostViewCnt,p.postCommSet,p.PostCon) " +
            "FROM PostEntity p WHERE p.PostCode = :postCode")
    PostResponse getDetail(@Param("postCode") String postCode);

    @Query("select c from CommentEntity c  where c.PostCode.PostCode = :postCode and c.CommStatus <> 'D'")
    List<CommonResponse> getCommentList(@Param("postCode") String postCode);

    @Query("select l from LowBoardEntity l")
    List<LowBoardEntity> getAllLowBoard();

    @Query("SELECT p FROM PostEntity p WHERE p.PostName LIKE CONCAT('%', :searchWord, '%') OR p.PostCon LIKE CONCAT('%', :searchWord, '%') AND p.FixStatus <> 'D' ORDER BY p.PostCode DESC")
    List<PostEntity> postSearch(String searchWord);

    @Query("select p from PostEntity p where p.PostCode = :postCode")
    PostEntity findByPostCode(String postCode);

    @Modifying
    @Query("DELETE FROM CommentEntity c WHERE c.CommCode = :commCode")
    Integer  deleteComment(@Param("CommCode") String commCode);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.CommCon = :commCon WHERE c.CommCode = :commCode")
    Integer updateComment(@Param("commCode") String commCode, @Param("commCon") String commCon);

    @Modifying
    @Query("UPDATE PostEntity p SET p.PostName = :postName, p.PostCon = :postCon, p.LowBoardCode.LowBoardCode = :lowBoardCode, p.postCommSet = :postCommSet, p.FixStatus = :fixStatus, p.NoticeStatus = :noticeStatus, p.PsCode.PsCode = :psCode WHERE p.PostCode = :postCode")
    Integer postUpdate(String postCode, String postName, String postCon, int lowBoardCode, PostCommSet postCommSet, char fixStatus, char noticeStatus, int psCode);

    @Modifying
    @Query("DELETE FROM AttachmentEntity a WHERE a.attachSort = :postCode")
    void deleteFile(@Param("postCode") String postCode);

    @Modifying
    @Query("UPDATE PostEntity p SET p.FixStatus='D' WHERE p.PostCode = :postCode")
    Integer postDelete(@Param("postCode") String postCode);

    @Query("SELECT p FROM PostEntity p WHERE p.FixStatus <> 'D' ORDER BY p.PostCode ")
    List<PostResponse> AllPostList(Pageable pageable);

    @Query("SELECT p from PostEntity p WHERE p.FixStatus = 'R' AND p.EmpCode.emp_code = :empCode ORDER BY p.PostCode DESC")
    List<PostRequest> ReadyPost(@Param("emp_code") int empCode);

    @Modifying
    @Query("update CommentEntity c set c.CommCon = :commCon where c.CommCode = :commCode")
    Integer commentEdit(@Param("commCode") String commCode, @Param("commCon") String commCon);

    @Modifying
    @Query("update CommentEntity c set c.CommStatus='D' where c.CommCode = :commCode")
    Integer commentDelete(String commCode);

    @Query("DELETE FROM PostRoleEntity p WHERE p.LowCode.LowBoardCode = :lowCode")
    @Modifying
    void deleteRoles(int lowCode);
}
