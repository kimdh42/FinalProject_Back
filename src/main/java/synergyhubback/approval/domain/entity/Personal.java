package synergyhubback.approval.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

@Entity
@Table(name = "APPROVAL_PERSONAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Personal {
    @Id
    private String apCode;
    private LocalDate apStart;
    private LocalDate apEnd;
    private String apContact;
    private String apReason;

    public static Personal of(String apCode, LocalDate apStart, LocalDate apEnd, String apContact, String apReason){
        return new Personal(apCode, apStart, apEnd, apContact, apReason);
    }

    public void modifyPersonal(LocalDate apStart, LocalDate apEnd, String apContact, String apReason){
        this.apStart = apStart;
        this.apEnd = apEnd;
        this.apContact = apContact;
        this.apReason = apReason;
    }
}
