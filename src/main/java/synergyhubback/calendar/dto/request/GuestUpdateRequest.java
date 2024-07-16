package synergyhubback.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestUpdateRequest {

    private int empCode; // 사원 코드

    private String eventCode; // 이벤트 코드

    private String taskCode; // 테스크 코드

    @JsonCreator
    public GuestUpdateRequest(
            @JsonProperty("empCode") int empCode,
            @JsonProperty("eventCode") String eventCode,
            @JsonProperty("taskCode") String taskCode) {
        this.empCode = empCode;
        this.eventCode = eventCode;
        this.taskCode = taskCode;
    }
}
