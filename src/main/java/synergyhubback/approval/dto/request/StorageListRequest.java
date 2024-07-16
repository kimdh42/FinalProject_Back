package synergyhubback.approval.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.Document;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class StorageListRequest {
    private List<Document> adCodeList;
}
