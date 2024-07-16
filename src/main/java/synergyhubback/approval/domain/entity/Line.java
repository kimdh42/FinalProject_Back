package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "APPROVAL_LINE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int alCode;
    private String alSort;
    private int alOrder;
    private String alRole;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lsCode")
    private LineSort lineSort;
}
