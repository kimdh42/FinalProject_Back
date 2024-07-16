package synergyhubback.common.event;

public class ApprovalCompletedEvent {

    private final String adCode;

    public ApprovalCompletedEvent(String adCode) {
        this.adCode = adCode;
    }

    public String getAdCode() {
        return adCode;
    }

}
