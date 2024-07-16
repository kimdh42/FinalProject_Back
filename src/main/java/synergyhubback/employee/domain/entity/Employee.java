package synergyhubback.employee.domain.entity;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "employee_info")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    @Id
    private int emp_code;

    private Integer par_code;       // 부모 사원 코드
    private String emp_name;
    private String emp_pass;
    private String social_security_no;
    private String email;
    private String phone;
    private String address;
    private int direct_line;
    private String account_num;
    private LocalDate hire_date;
    private LocalDate end_date;
    private String emp_status;
    private Integer emp_sign;
    private String emp_img;
    private String refreshToken;    // 생성함




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_code", referencedColumnName = "dept_code")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_code", referencedColumnName = "title_code")
    private Title title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_code", referencedColumnName = "position_code")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_code", referencedColumnName = "bank_code")
    private Bank bank;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<DetailByEmpRegist> empRegistDetails = new HashSet<>();

    public Employee(int emp_code, String emp_name, String emp_pass, String social_security_no, String email, LocalDate hire_date, String emp_status) {
        this.emp_code = emp_code;
        this.emp_name = emp_name;
        this.emp_pass = emp_pass;
        this.social_security_no = social_security_no;
        this.email = email;
        this.hire_date = hire_date;
        this.emp_status = emp_status;
    }

    public Employee(int empCode, String empName, String email, String phone, String address, String accountNum, String empImg, Bank bank) {
    }


    public static Employee regist(int emp_code, String emp_name, String emp_pass, String social_security_no, String email, LocalDate hire_date, String emp_status) {

        return new Employee(
                emp_code,
                emp_name,
                emp_pass,
                social_security_no,
                email,
                hire_date,
                emp_status
        );
    }

    public void update(String email, String phone, String address, String emp_pass, Bank bank, String account_num) {
        if (email != null) {
            this.email = email;
        }
        if (phone != null) {
            this.phone = phone;
        }
        if (address != null) {
            this.address = address;
        }
        if (emp_pass != null) {
            this.emp_pass = emp_pass;
        }
        if (bank != null) {
            this.bank = bank;
        }
        if (account_num != null) {
            this.account_num = account_num;
        }
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public void addEmpRegistDetail(DetailByEmpRegist detailByEmpRegist) {
        empRegistDetails.add(detailByEmpRegist);
    }

    public void resetPassword(String resetEmpPass) {
        this.emp_pass = resetEmpPass;
    }

    public void updatePassword(String newEmpPass) {
        this.emp_pass = newEmpPass;
    }

    // 이재현 로그인 관련 employee entity 로직 생성
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        System.out.println("updateRefreshToken");
    }

    public void setEmp_code(int emp_code) {
        this.emp_code = emp_code;
    }
    // 이다정: 서명등록
    public void signRegist(Integer emp_sign) {
        this.emp_sign = emp_sign;
    }


    public void setEmpCode(int empCode) {
        this.emp_code = empCode;
    }

    public void profileImgRegist(String emp_img) {
        this.emp_img = emp_img;
    }
}
