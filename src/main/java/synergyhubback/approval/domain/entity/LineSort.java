package synergyhubback.approval.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LINE_SORT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LineSort {
    @Id
    private int lsCode;
    private String lsName;
}
