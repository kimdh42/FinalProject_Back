package synergyhubback.post.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.post.dto.request.CommontRequest;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "comment")
@Getter
@ToString
@Setter
@NoArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comm_code")
    private int CommCode;
    @Column(name = "comm_con")

    private String CommCon;
    @Column(name = "comm_date")
    private LocalDateTime CommDate;
    @Column(name = "comm_status")

    private String CommStatus;

    @ManyToOne
    @JoinColumn(name = "Post_Code")
    private PostEntity PostCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Emp_Code")
    @JsonIgnore
    private Employee EmpCode;

    @Builder
    public CommentEntity(int commCode, String commCon, LocalDateTime commDate, String commStatus, PostEntity postCode, Employee empCode) {
        CommCode = commCode;
        CommCon = commCon;
        CommDate = commDate;
        CommStatus = commStatus;
        PostCode = postCode;
        EmpCode = empCode;
    }
}
