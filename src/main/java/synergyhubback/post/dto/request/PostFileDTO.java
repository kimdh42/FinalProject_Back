package synergyhubback.post.dto.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import synergyhubback.post.domain.entity.LowBoardEntity;
import synergyhubback.post.domain.entity.PostSortEntity;
import synergyhubback.post.domain.type.PostCommSet;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class PostFileDTO {

    private int FileCode;
    private String savedName;
    private String savePath;

    private String PostCode;
    private String PostName;
    private int PostViewCnt;
    private LocalDate PostDate;
    private String PostCon;
    private PostCommSet postCommSet;
    private char FixStatus;
    private char NoticeStatus;


    private int EmpCode;
    private LowBoardEntity LowBoardCode;
    private PostSortEntity PsCode;


}
