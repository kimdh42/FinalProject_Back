package synergyhubback.post.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.post.domain.type.PostCommSet;
import synergyhubback.post.dto.request.PostRequest;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class PostEntity {
    @Id
    @Column(name = "POST_CODE")
    private String PostCode;
    @Column(name = "post_name")
    private String PostName;
    @Column(name = "post_view_cnt")
    private int PostViewCnt;
    @Column(name = "post_date")
    private LocalDate PostDate;
    @Column(name = "post_con")
    private String PostCon;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "post_comm_set", columnDefinition = "int")
    private PostCommSet postCommSet;
    @Column(name = "fix_status")
    private char FixStatus;
    @Column(name = "notice_status")
    private char NoticeStatus;
    @ManyToOne
    @JoinColumn(name = "emp_code")
    private Employee EmpCode;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "Low_Code")
    private LowBoardEntity LowBoardCode;
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "Ps_Code")
    private PostSortEntity PsCode;

    @Override
    public String toString() {
        return "PostEntity{" +
                "PostCode='" + PostCode + '\'' +
                ", PostName='" + PostName + '\'' +
                ", PostViewCnt=" + PostViewCnt +
                ", PostDate=" + PostDate +
                ", PostCon='" + PostCon + '\'' +
                ", postCommSet=" + postCommSet +
                ", FixStatus=" + FixStatus +
                ", NoticeStatus=" + NoticeStatus +
                ", EmpCode=" + (EmpCode != null ? EmpCode.getEmp_code() : null) +
                ", LowBoardCode=" + (LowBoardCode != null ? LowBoardCode.getLowBoardCode() : null) +
                ", PsCode=" + (PsCode != null ? PsCode.getPsCode() : null) +
                '}';
    }

    public PostRequest toPostRequest() {
        PostRequest postRequest = new PostRequest();
        postRequest.setPostCode(this.PostCode);
        postRequest.setEmpCode(this.EmpCode.getEmp_code());
        postRequest.setPostName(this.PostName);
        postRequest.setPostCon(this.PostCon);
        postRequest.setPostDate(this.PostDate);
        // 필요한 다른 필드를 설정
        return postRequest;
    }

}
