package synergyhubback.approval.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.common.attachment.AttachmentEntity;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AttachmentResponse {
    private final String attachOriginal;
    private final String attachSave;
    private final String attachUrl;
    private final String attachSort;

    public static AttachmentResponse from(final AttachmentEntity attachment){
        return new AttachmentResponse(
                attachment.getAttachOriginal(),
                attachment.getAttachSave(),
                attachment.getAttachUrl(),
                attachment.getAttachSort()
        );
    }
}
