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
@Table(name = "emp_title")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Title {

    @Id
    private String title_code;

    private String title_name;

    public Title(String title_code, String title_name) {
        this.title_code = title_code;
        this.title_name = title_name;
    }
}
