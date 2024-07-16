package synergyhubback.calendar.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LABEL")
@Getter
@Setter
@NoArgsConstructor
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LABEL_CODE")
    private Long id;

    @Column(name = "LABEL_TITLE")
    private String labelTitle;

    @Column(name = "LABEL_CON")
    private String labelCon;

    @Column(name = "LABEL_COLOR")
    private String labelColor;
}
