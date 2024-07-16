package synergyhubback.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import synergyhubback.pheed.domain.entity.Pheed;
import synergyhubback.pheed.dto.response.PheedResponse;
import synergyhubback.pheed.presentation.PheedController;
import synergyhubback.pheed.service.PheedService;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class PheedEvent {

    private final PheedService pheedService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePheedCreatedEvent(PheedCreatedEvent event) {
        Pheed createdPheed = event.getCreatedPheed();
        sendNewPheedNotificationToClients(createdPheed);
    }

    private void sendNewPheedNotificationToClients(Pheed pheed) {
        for (Iterator<SseEmitter> iterator = PheedController.sseEmitters.values().iterator(); iterator.hasNext();) {
            SseEmitter sseEmitter = iterator.next();
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("newPheed")
                        .data(convertToPheedResponse(pheed)));
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
                iterator.remove(); // 예외가 발생한 Emitter를 제거
            }
        }
    }

    private PheedResponse convertToPheedResponse(Pheed pheed) {
        return PheedResponse.builder()
                .pheedCode(pheed.getPheedCode())
                .pheedCon(pheed.getPheedCon())
                .creStatus(pheed.getCreStatus())
                .readStatus(pheed.getReadStatus())
                .deStatus(pheed.getDeStatus())
                .pheedSort(pheed.getPheedSort())
                .empCode(pheed.getEmployee().getEmp_code())
                .url(pheed.getUrl())
                .build();
    }

}


