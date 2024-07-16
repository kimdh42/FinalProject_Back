package synergyhubback.post.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import synergyhubback.post.domain.entity.CommentEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CommontRequest {
    private int commCode;
    private String commCon;
    private LocalDateTime commDate;
    private String commStatus;
    private String postCode;  // 여전히 String 타입
    private int emp_code;    // 여전히 String 타입

//    @JsonCreator
//    public CommontRequest(CommentEntity commentEntity) {
//        this.commCon = commentEntity.getCommCon();
//        this.commDate = commentEntity.getCommDate();
//        this.commStatus = commentEntity.getCommStatus();
//        this.postCode = commentEntity.getPostCode().getPostCode();
//        this.emp_code = commentEntity.getEmpCode().getEmp_code();
//    }


    @JsonCreator
    public CommontRequest(@JsonProperty("commCon") String commCon,
                          @JsonProperty("commData") LocalDateTime commDate,
                          @JsonProperty("commStatus") String commStatus,
                          @JsonProperty("postCode") String postCode,
                          @JsonProperty("emp_code") int emp_code) {

        this.commCon = commCon;
        this.commDate = commDate;
        this.commStatus = commStatus;
        this.postCode = postCode;
        this.emp_code = emp_code;
    }
}
