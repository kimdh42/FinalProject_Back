package synergyhubback.employee.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "emp_position")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Position {

    @Id
    private String position_code;

    private String position_name;

    public Position(String position_code, String position_name) {
        this.position_code = position_code;
        this.position_name = position_name;
    }
}
