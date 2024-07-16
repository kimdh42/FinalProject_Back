package synergyhubback.common.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ATTACHMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AttachmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attachCode;
    private String attachOriginal;
    private String attachSave;
    private String attachUrl;
    @JsonIgnore
    private String attachSort;

    public AttachmentEntity(String attachOriginal, String attachSave, String attachUrl, String attachSort){
        this.attachOriginal = attachOriginal;
        this.attachSave = attachSave;
        this.attachUrl = attachUrl;
        this.attachSort = attachSort;
    }
    public static AttachmentEntity of(String attachOriginal, String attachSave, String attachUrl, String attachSort){
        return new AttachmentEntity(attachOriginal, attachSave, attachUrl, attachSort);
    }

    public void modifyAttachment(String attachOriginal, String attachSave){
        this.attachOriginal = attachOriginal;
        this.attachSave = attachSave;
    }
    public static AttachmentEntity create(String attachOriginal, String attachSave, String attachUrl, String attachSort) {

        return new AttachmentEntity(
                attachOriginal,
                attachSave,
                attachUrl,
                attachSort
        );
    }
}


