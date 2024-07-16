package synergyhubback.message.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "storage")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int storCode;
    private String storName;

    public void setStorCode(int storCode) {
        this.storCode = storCode;
    }
}
