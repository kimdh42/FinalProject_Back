package synergyhubback.approval.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;

@Entity
@Table(name = "APPROVAL_DOC")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Document {
    @Id
    private String adCode;
    private String adTitle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_code")
    private Employee employee;

    private LocalDate adReportDate;
    private String adStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "afCode")
    private Form form;

    private String adDetail;

    public static Document of(String adCode, String adTitle, Employee employee, LocalDate adReportDate, String adStatus, Form form, String adDetail) {
        return new Document(adCode, adTitle, employee, adReportDate, adStatus, form, adDetail);
    }

    public void modifyAdStatus(String adStatus) {
        this.adStatus = adStatus;
    }

    public void modifyDocument(String adTitle, LocalDate adReportDate, String adStatus){
        this.adTitle = adTitle;
        this.adReportDate = adReportDate;
        this.adStatus = adStatus;
    }

    public void modifyStatus(String adStatus){
        this.adStatus = adStatus;
    }
}
