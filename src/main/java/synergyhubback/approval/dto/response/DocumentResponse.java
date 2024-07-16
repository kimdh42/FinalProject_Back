package synergyhubback.approval.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import synergyhubback.approval.domain.entity.Document;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentResponse {
    private final String adCode;
    private final String adTitle;
    private final int emp_code;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate adReportDate;
    private final String adStatus;
    private final String afName;
    private final String adDetail;

    public static DocumentResponse from(final Document document){
        return new DocumentResponse(
                document.getAdCode(),
                document.getAdTitle(),
                document.getEmployee().getEmp_code(),
                document.getAdReportDate(),
                document.getAdStatus(),
                document.getForm().getAfName(),
                document.getAdDetail()
        );
    }

}
