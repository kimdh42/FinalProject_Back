package synergyhubback.post.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import synergyhubback.post.domain.entity.BoardEntity;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LowBoardResponse {
    private int LowBoardCode;
    private String LowBoardName;
    private BoardEntity BoardCode;

}
