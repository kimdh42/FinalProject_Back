package synergyhubback.approval.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import synergyhubback.approval.dto.response.LineEmpDTO;

import java.util.List;


//커밋생성
@Mapper
public interface LineEmpMapper {
    List<LineEmpDTO> findLineEmpList(@Param("depCode") String deptCode, @Param("titleCode") String titleCode, @Param("lsCode") Integer lsCode);
}
