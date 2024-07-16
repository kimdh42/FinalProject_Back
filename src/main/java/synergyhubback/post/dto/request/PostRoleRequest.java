package synergyhubback.post.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.post.domain.entity.LowBoardEntity;
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PostRoleRequest {
    private char PrWriteRole;
    private int LowCode;
    private int EmpCode;
    private char PrAdmin;


    @JsonCreator
    public PostRoleRequest(@JsonProperty("prWriteRole") char prWriteRole,@JsonProperty("lowCode") int lowCode,@JsonProperty("empCode") int empCode,@JsonProperty("prAdmin") char prAdmin) {
        this.PrWriteRole = prWriteRole;
        this.LowCode = lowCode;
        this.EmpCode = empCode;
        this.PrAdmin = prAdmin;
    }

    public PostRoleRequest(char prWriteRole, LowBoardEntity lowCode, int empCode, char prAdmin, String empName, String deptTitle, String positionName) {
        this.PrWriteRole = prWriteRole;
        this.LowCode = lowCode.getLowBoardCode();
        this.EmpCode = empCode;
        this.PrAdmin = prAdmin;

    }
}
