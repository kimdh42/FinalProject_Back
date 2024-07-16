package synergyhubback.post.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "low_board")
@Getter
@Setter
public class LowBoardEntity {
    @Id
    @Column(name = "low_code")
    private int LowBoardCode;
    @Column(name = "low_name")
    private String LowBoardName;
    @OneToOne
    @JoinColumn(name = "Board_Code")
    private BoardEntity BoardCode;

}
