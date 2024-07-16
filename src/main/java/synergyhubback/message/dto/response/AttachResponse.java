package synergyhubback.message.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.common.attachment.AttachmentEntity;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AttachResponse {

    private final String attachOriginal;
    private final String attachSave;
    private final String attachUrl;
    private final String attachSort;

    public static AttachResponse find(AttachmentEntity attachment) {
        return new AttachResponse(
                attachment.getAttachOriginal(),
                attachment.getAttachSave(),
                attachment.getAttachUrl(),
                attachment.getAttachSort()
        );
    }
}
