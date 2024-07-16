package synergyhubback.calendar.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestResponse {

    private String guestCode;
    private int empCode;
    private String eventCode;
    private String taskCode;

    public GuestResponse() {
    }

    public GuestResponse(String guestCode, int empCode, String eventCode, String taskCode) {
        this.guestCode = guestCode;
        this.empCode = empCode;
        this.eventCode = eventCode;
        this.taskCode = taskCode;
    }
}
