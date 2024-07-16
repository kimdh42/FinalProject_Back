package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "APPROVAL_ETC")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Etc {
    @Id
    private String aeCode;
    @Column(columnDefinition = "LONGTEXT")
    private String aeCon;

    public static Etc of(String aeCode, String aeCon){
        return new Etc(aeCode, aeCon);
    }

    public void modifyEtc(String aeCon){
        this.aeCon = aeCon;
    }
}
