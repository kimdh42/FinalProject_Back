package synergyhubback.message.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import synergyhubback.message.domain.entity.MessageBlock;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BlockEmpResponse {

    private int blkCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate blkDate;
    private String blkId;
    private String blkName;

    public static BlockEmpResponse getBlockEmp(MessageBlock messageBlock) {

        return new BlockEmpResponse(
                messageBlock.getBlkCode(),
                messageBlock.getBlkDate(),
                messageBlock.getBlkId().getEmp_name(),
                messageBlock.getBlkName().getEmp_name()
        );
    }
}
