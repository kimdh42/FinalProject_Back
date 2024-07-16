package synergyhubback.employee.domain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import synergyhubback.employee.dto.request.RegistSchoolInfoRequest;

import java.time.LocalDate;

@Entity
@Table(name = "shcool_info")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SchoolInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sch_code;

    private String sch_name;

    private String grad_status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate enrole_date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate grad_date;

    private String major;

    private String day_n_night;

    private String location;

    private int emp_code;

    public SchoolInfo(String sch_name, String grad_status, LocalDate enrole_date, LocalDate grad_date, String major, String day_n_night, String location, int emp_code) {
        this.sch_name = sch_name;
        this.grad_status = grad_status;
        this.enrole_date = enrole_date;
        this.grad_date = grad_date;
        this.major = major;
        this.day_n_night = day_n_night;
        this.location = location;
        this.emp_code = emp_code;
    }

    public void updateSchoolInfo(RegistSchoolInfoRequest schoolInfoRequest) {
        if (schoolInfoRequest.getSch_name() != null) {
            this.sch_name = schoolInfoRequest.getSch_name();
        }
        if (schoolInfoRequest.getGrad_status() != null) {
            this.grad_status = schoolInfoRequest.getGrad_status();
        }
        if (schoolInfoRequest.getEnrole_date() != null) {
            this.enrole_date = schoolInfoRequest.getEnrole_date();
        }
        if (schoolInfoRequest.getGrad_date() != null) {
            this.grad_date = schoolInfoRequest.getGrad_date();
        }
        if (schoolInfoRequest.getMajor() != null) {
            this.major = schoolInfoRequest.getMajor();
        }
        if (schoolInfoRequest.getDay_n_night() != null) {
            this.day_n_night = schoolInfoRequest.getDay_n_night();
        }
        if (schoolInfoRequest.getLocation() != null) {
            this.location = schoolInfoRequest.getLocation();
        }
    }
}
