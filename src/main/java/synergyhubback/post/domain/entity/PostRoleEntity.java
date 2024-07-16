package synergyhubback.post.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;

@Entity
@Table(name = "post_role")
@Getter
@Setter
public class PostRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pr_code")
    private int PrCode;
    @Column(name = "pr_write_role")
    private char PrWriteRole;
    @OneToOne
    @JoinColumn(name = "Low_Code")
    private LowBoardEntity LowCode;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Emp_Code")
    private Employee EmpCode;
    @Column(name = "pr_admin")
    private char PrAdmin;


}
