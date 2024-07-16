package synergyhubback.post.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import synergyhubback.post.domain.entity.LowBoardEntity;
import synergyhubback.post.domain.entity.PostSortEntity;
import synergyhubback.post.domain.type.PostCommSet;

import java.time.LocalDate;
import java.util.Date;

@Getter
@RequiredArgsConstructor
@Setter
public class PostRequest {
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

    public void setLowBoardCode(int lowBoardCode) {
        LowBoardEntity lowBoardEntity = new LowBoardEntity();
        lowBoardEntity.setLowBoardCode(lowBoardCode);
        this.LowBoardCode = lowBoardEntity;
    }

    public void setPsCode(int psCode) {
        PostSortEntity postSortEntity = new PostSortEntity();
        postSortEntity.setPsCode(psCode);
        this.PsCode = postSortEntity;
    }

    @JsonCreator
    public PostRequest(@JsonProperty("postcode") String postCode,@JsonProperty("postName") String postName,@JsonProperty("postViewCnt") int postViewCnt,@JsonProperty("postDate") LocalDate postDate,@JsonProperty("postCon") String postCon,@JsonProperty("postCommSet") PostCommSet postCommSet,@JsonProperty("fixStatus") char fixStatus,@JsonProperty("noticeStatus") char noticeStatus,@JsonProperty("empCode") int empCode, @JsonProperty("lowBoardCode") LowBoardEntity lowBoardCode,@JsonProperty("psCode") PostSortEntity psCode) {
        this.PostCode = postCode;
        this.PostName = postName;
        this.PostViewCnt = postViewCnt;
        this.PostDate = postDate;
        this.PostCon = postCon;
        this.postCommSet = postCommSet;
        this.FixStatus = fixStatus;
        this.NoticeStatus = noticeStatus;
        this.EmpCode = empCode;
        this.LowBoardCode = lowBoardCode;
        this.PsCode = psCode;
    }
}
