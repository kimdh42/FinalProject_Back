package synergyhubback.post.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor


public class BoardRequest {
    private int lowCode;
    private int boardCode;
    private String lowName;

}
