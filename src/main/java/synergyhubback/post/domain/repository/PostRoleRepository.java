package synergyhubback.post.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.post.domain.entity.PostRoleEntity;
import synergyhubback.post.dto.request.PostRoleRequest;
import synergyhubback.post.dto.request.PostRollRequest;
import synergyhubback.post.dto.response.PostResponse;
import synergyhubback.post.dto.response.PostRoleResponse;

import java.util.List;

@Repository
public interface PostRoleRepository extends JpaRepository<PostRoleEntity, Long> {
    @Query("select new synergyhubback.post.dto.request.PostRollRequest(" +
            "  pr.PrWriteRole, pr.LowCode, e.emp_code, pr.PrAdmin, " +
            "  e.emp_name, d.dept_title, pos.position_name) " +
            "from PostRoleEntity pr " +
            "join pr.EmpCode e " +
            "left join e.department d " +
            "left join e.position pos " +
            "where pr.LowCode.LowBoardCode = :LowBoardCode " +
            "and ((:roll = 'write' and pr.PrWriteRole = 'Y') " +
            "or (:roll = 'Read' and pr.PrWriteRole = 'N' and pr.PrAdmin = 'N' ) " +
            "or (:roll = 'Admin' and pr.PrAdmin = 'Y'))")
    List<PostRollRequest> findByLowBoardCodeAndRoll(@Param("LowBoardCode") int LowBoardCode, @Param("roll") String roll);

    @Query("select new synergyhubback.post.dto.request.PostRollRequest(pr.PrWriteRole, pr.LowCode, pr.EmpCode.emp_code, pr.PrAdmin, pr.EmpCode.emp_name, pr.EmpCode.department.dept_title, pr.EmpCode.position.position_code) " +
            "from PostRoleEntity pr")
    List<PostRollRequest> findAllRole();
}
