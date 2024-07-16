package synergyhubback.message.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import synergyhubback.employee.domain.entity.Employee;

import java.time.LocalDate;

@Entity
@Table(name = "message_block")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class MessageBlock {

    @Id
    private int blkCode;
    private LocalDate blkDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLK_ID", referencedColumnName = "emp_code")
    private Employee blkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLK_NAME", referencedColumnName = "emp_code")
    private Employee blkName;

    public void setBlkId(Employee blkId) {
        this.blkId = blkId;
    }

    public void setBlkName(Employee blkName) {
        this.blkName = blkName;
    }

    public MessageBlock(int blkCode, LocalDate blkDate) {
        this.blkCode = blkCode;
        this.blkDate = blkDate;
    }

    public static MessageBlock create(int blkCode, LocalDate blkDate) {

        return new MessageBlock(
                blkCode,
                blkDate
        );
    }
}
