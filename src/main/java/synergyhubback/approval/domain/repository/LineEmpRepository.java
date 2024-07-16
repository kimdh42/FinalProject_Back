package synergyhubback.approval.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import synergyhubback.approval.dto.response.LineEmpDTO;

import java.util.List;

@Repository
public interface LineEmpRepository {
    @Query(value = "WITH RECURSIVE DeptHierarchy AS (" +
            "    SELECT PAR_DEPT_CODE, SUB_DEPT_CODE " +
            "    FROM DEPT_RELATIONS " +
            "    WHERE SUB_DEPT_CODE = :depCode " +
            "    UNION ALL " +
            "    SELECT dr.PAR_DEPT_CODE, dr.SUB_DEPT_CODE " +
            "    FROM DEPT_RELATIONS dr " +
            "    JOIN DeptHierarchy dh ON dr.SUB_DEPT_CODE = dh.PAR_DEPT_CODE " +
            ") " +
            "SELECT ei.*, et.TITLE_NAME, dp.DEPT_TITLE " +
            "FROM ( " +
            "    SELECT ei.*, " +
            "           ROW_NUMBER() OVER (PARTITION BY ei.TITLE_CODE ORDER BY CAST(SUBSTRING_INDEX(ei.DEPT_CODE, 'D', -1) AS UNSIGNED) DESC) AS rn " +
            "    FROM EMPLOYEE_INFO ei " +
            "    JOIN ( " +
            "        SELECT DISTINCT PAR_DEPT_CODE AS DEPT_CODE " +
            "        FROM DeptHierarchy " +
            "        UNION " +
            "        SELECT DISTINCT SUB_DEPT_CODE AS DEPT_CODE " +
            "        FROM DeptHierarchy " +
            "    ) AS dept_codes ON ei.DEPT_CODE = dept_codes.DEPT_CODE " +
            "    WHERE ei.TITLE_CODE < :titleCode " +
            ") AS ei " +
            "JOIN EMP_TITLE et ON ei.TITLE_CODE = et.TITLE_CODE " +
            "JOIN DEPARTMENT dp ON ei.DEPT_CODE = dp.DEPT_CODE " +
            "WHERE ei.rn = 1 " +
            "AND ei.EMP_STATUS = 'Y' " +
            "ORDER BY CAST(SUBSTRING_INDEX(ei.DEPT_CODE, 'D', -1) AS UNSIGNED) DESC, ei.TITLE_CODE DESC",
            nativeQuery = true)
    List<LineEmpDTO> findLineEmpList(String depCode, String titleCode);
}
