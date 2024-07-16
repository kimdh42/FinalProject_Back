package synergyhubback.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.post.domain.entity.CommentEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private int comm_code;

    private LocalDateTime commDate;

    private String comm_con;

    private String comm_status;

    private String post_code;

    private int emp_code;
    private String emp_name;


    public CommonResponse(CommentEntity commentEntity) {
        this.comm_code = commentEntity.getCommCode();
        this.commDate = commentEntity.getCommDate();
        this.comm_con = commentEntity.getCommCon();
        this.comm_status = commentEntity.getCommStatus();
        this.post_code = commentEntity.getPostCode().getPostCode();
        this.emp_code = commentEntity.getEmpCode().getEmp_code();
        this.emp_name = commentEntity.getEmpCode().getEmp_name();
    }

}
