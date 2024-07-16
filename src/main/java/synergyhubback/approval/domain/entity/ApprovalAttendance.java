package synergyhubback.approval.domain.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "APPROVAL_ATTENDANCE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalAttendance {
    @Id
    private String aattCode;
    private String aattSort;
    private LocalDateTime aattStart;
    private LocalDateTime aattEnd;
    private LocalDate aattOccur;
    private String aattPlace;
    private String aattCon;
    private String aattReason;

    private ApprovalAttendance(String aattCode, String aattSort, LocalDateTime aattStart, LocalDateTime aattEnd, String aattPlace, String aattCon){
        this.aattCode = aattCode;
        this.aattSort = aattSort;
        this.aattStart = aattStart;
        this.aattEnd = aattEnd;
        this.aattPlace = aattPlace;
        this.aattCon = aattCon;
    }
    public static ApprovalAttendance of(String aattCode, String aattSort, LocalDateTime aattStart, LocalDateTime aattEnd, String aattPlace, String aattCon){
        return new ApprovalAttendance(aattCode, aattSort, aattStart, aattEnd, aattPlace, aattCon);
    }

    private ApprovalAttendance(String aattCode, String aattReason, String aattSort, LocalDateTime aattStart, LocalDateTime aattEnd, String aattPlace, String aattCon){
        this.aattCode = aattCode;
        this.aattReason = aattReason;
        this.aattSort = aattSort;
        this.aattStart = aattStart;
        this.aattEnd = aattEnd;
        this.aattPlace = aattPlace;
        this.aattCon = aattCon;
    }
    public static ApprovalAttendance of(String aattCode, String aattReason, String aattSort, LocalDateTime aattStart, LocalDateTime aattEnd, String aattPlace, String aattCon){
        return new ApprovalAttendance(aattCode, aattReason, aattSort, aattStart, aattEnd, aattPlace, aattCon);
    }

    private ApprovalAttendance(String aattCode, String aattSort, LocalDate aattOccur, String aattReason){
        this.aattCode = aattCode;
        this.aattSort = aattSort;
        this.aattOccur = aattOccur;
        this.aattReason = aattReason;
    }
    public static ApprovalAttendance of(String aattCode, String aattSort, LocalDate aattOccur, String aattReason){
        return new ApprovalAttendance(aattCode, aattSort, aattOccur, aattReason);
    }

    private ApprovalAttendance(String aattCode, String aattSort, LocalDateTime aattStart, LocalDateTime aattEnd){
        this.aattCode = aattCode;
        this.aattSort = aattSort;
        this.aattStart = aattStart;
        this.aattEnd = aattEnd;
    }
    public static ApprovalAttendance of(String aattCode, String aattSort, LocalDateTime aattStart, LocalDateTime aattEnd){
        return new ApprovalAttendance(aattCode, aattSort, aattStart, aattEnd);
    }

    public void modifyApprovalAttendance(String aattSort, LocalDateTime aattStart, LocalDateTime aattEnd, LocalDate aattOccur, String aattPlace, String aattCon, String aattReason){
        this.aattSort = aattSort;
        this.aattStart = aattStart;
        this.aattEnd = aattEnd;
        this.aattOccur = aattOccur;
        this.aattPlace = aattPlace;
        this.aattCon = aattCon;
        this.aattReason = aattReason;
    }
}
