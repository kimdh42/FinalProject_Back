package synergyhubback.post.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "post_sort")
@Getter
@Setter
public class PostSortEntity {
    @Id
    @Column(name = "ps_code")
    private int PsCode;
    @Column(name = "ps_name")
    private String PsName;

}
