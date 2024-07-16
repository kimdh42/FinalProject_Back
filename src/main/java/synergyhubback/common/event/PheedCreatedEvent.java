package synergyhubback.common.event;

import org.springframework.context.ApplicationEvent;
import synergyhubback.pheed.domain.entity.Pheed;

public class PheedCreatedEvent extends ApplicationEvent {

    private final Pheed createdPheed;

    public PheedCreatedEvent(Object source, Pheed createdPheed) {
        super(source);
        this.createdPheed = createdPheed;
    }

    public Pheed getCreatedPheed() {
        return createdPheed;
    }

}
