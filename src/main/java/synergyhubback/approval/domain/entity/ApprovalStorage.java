package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "APPROVAL_STORAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalStorage {
    @Id
    private int asCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adCode")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abCode")
    private ApprovalBox approvalBox;

    private ApprovalStorage(Document document, ApprovalBox approvalBox){
        this.document = document;
        this.approvalBox = approvalBox;
    }

    public static ApprovalStorage of(Document document, ApprovalBox approvalBox){
        return new ApprovalStorage(document, approvalBox);
    }
}
