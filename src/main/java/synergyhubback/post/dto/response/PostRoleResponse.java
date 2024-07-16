package synergyhubback.post.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.post.domain.entity.LowBoardEntity;
import synergyhubback.post.domain.entity.PostRoleEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRoleResponse {
    private int PrCode;
    private char PrWriteRole;
    private LowBoardEntity LowCode;
    private Employee EmpCode;
    private char PrAdmin;

    public PostRoleResponse(PostRoleEntity postRoleEntity) {
        this.PrCode = postRoleEntity.getPrCode();
        this.PrWriteRole = postRoleEntity.getPrWriteRole();
        this.LowCode = postRoleEntity.getLowCode();
        this.EmpCode = postRoleEntity.getEmpCode();
        this.PrAdmin = postRoleEntity.getPrAdmin();
    }
}
