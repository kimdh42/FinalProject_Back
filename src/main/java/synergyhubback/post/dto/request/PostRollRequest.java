package synergyhubback.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import synergyhubback.post.domain.entity.LowBoardEntity;
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PostRollRequest {
    private char prWriteRole;
    private LowBoardEntity lowCode;
    private int emp_Code; // assuming emp_code is an int
    private char prAdmin;
    private String emp_Name;
    private String dept_title;
    private String position_name;

    public PostRollRequest(char prWriteRole, LowBoardEntity lowCode, int emp_Code, char prAdmin, String emp_Name, String dept_title, String position_name) {
        this.prWriteRole = prWriteRole;
        this.lowCode = lowCode;
        this.emp_Code = emp_Code;
        this.prAdmin = prAdmin;
        this.emp_Name = emp_Name;
        this.dept_title = dept_title;
        this.position_name = position_name;
    }

    // Getters and setters (생성되거나 수동으로 구현)

    public char getPrWriteRole() {
        return prWriteRole;
    }

    public void setPrWriteRole(char prWriteRole) {
        this.prWriteRole = prWriteRole;
    }

    public LowBoardEntity getLowCode() {
        return lowCode;
    }

    public void setLowCode(LowBoardEntity lowCode) {
        this.lowCode = lowCode;
    }

    public int getEmp_Code() {
        return emp_Code;
    }

    public void setEmp_Code(int emp_Code) {
        this.emp_Code = emp_Code;
    }

    public char getPrAdmin() {
        return prAdmin;
    }

    public void setPrAdmin(char prAdmin) {
        this.prAdmin = prAdmin;
    }

    public String getEmp_Name() {
        return emp_Name;
    }

    public void setEmp_Name(String emp_Name) {
        this.emp_Name = emp_Name;
    }

    public String getDept_title() {
        return dept_title;
    }

    public void setDept_title(String dept_title) {
        this.dept_title = dept_title;
    }

    public String getPosition_name() {
        return position_name;
    }

    public void setPosition_name(String position_name) {
        this.position_name = position_name;
    }
}
