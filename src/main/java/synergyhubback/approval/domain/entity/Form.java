package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Clob;

@Entity
@Table(name = "APPROVAL_FORM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int afCode;
    private String afName;
    private String afExplain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lsCode")
    private LineSort lineSort;

    @Column(columnDefinition = "LONGTEXT")
    private String afCon;

    private char afActive;

    public Form(String afName, String afExplain, LineSort lineSort, String afCon, char afActive){
        this.afName = afName;
        this.afExplain = afExplain;
        this.lineSort = lineSort;
        this.afCon = afCon;
        this.afActive = afActive;
    }

    public static Form of(String afName, String afExplain, LineSort lineSort, String afCon, char afActive){
        return new Form(afName, afExplain, lineSort, afCon, afActive);
    }

    public void modifyForm(String afName, String afExplain, LineSort lineSort, String afCon){
        this.afName = afName;
        this.afExplain = afExplain;
        this.lineSort = lineSort;
        this.afCon = afCon;
    }

    public void nonActiveForm(char afActive){
        this.afActive = afActive;
    }
}
