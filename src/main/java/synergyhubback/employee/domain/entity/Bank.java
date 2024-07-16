package synergyhubback.employee.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "bank")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Bank {

    @Id
    private int bank_code;

    private String bank_name;

    public Bank(int bank_code, String bank_name) {
        this.bank_code = bank_code;
        this.bank_name = bank_name;
    }
}
