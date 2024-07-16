package synergyhubback.pheed.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import synergyhubback.common.event.PheedCreatedEvent;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.repository.EmployeeRepository;
import synergyhubback.pheed.domain.entity.Pheed;
import synergyhubback.pheed.domain.repository.PheedRepository;
import synergyhubback.pheed.domain.repository.SseRepository;
import synergyhubback.pheed.dto.response.PheedResponse;
import synergyhubback.pheed.presentation.PheedController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PheedService {

    private static final Long DEFAULT_TIMEOUT = 7L * 24 * 60 * 60 * 1000;

    private final EmployeeRepository employeeRepository;
    private final SseRepository sseRepository;
    private final PheedRepository pheedRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SseEmitter subscribe(String empCode, String lastEventId) {
        String emitterId = empCode + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = sseRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        System.out.println("New emitter added: " + sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PheedController.sseEmitters.put(String.valueOf(empCode), sseEmitter);

        sseEmitter.onCompletion(() -> sseRepository.deleteEmitterById(emitterId));
        sseEmitter.onTimeout(() -> sseRepository.deleteEmitterById(emitterId));
        sseEmitter.onError(e -> sseRepository.deleteEmitterById(emitterId));

        send(sseEmitter, emitterId, createDummyPheed(empCode));

        if (hasLostData(lastEventId)) {
            Map<String, Object> eventCaches = sseRepository.findAllEventCacheStartsWithUsername(empCode);
            eventCaches.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> emitEventToClient(sseEmitter, entry.getKey(), entry.getValue()));
        }

        return sseEmitter;
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void send(SseEmitter sseEmitter, String emitterId, PheedResponse pheedResponse) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(pheedResponse, MediaType.APPLICATION_JSON));
        } catch(IOException exception) {
            sseRepository.deleteEmitterById(emitterId);
            sseEmitter.completeWithError(exception);
        }
    }

    public void send(String con, String sort, String empCode, String url) {
        PheedResponse pheed = pheedCreateRequest(con, sort, empCode, url);

        Map<String, SseEmitter> sseEmitters = sseRepository.findAllEmitterStartsWithUsername(empCode);
        sseEmitters.forEach((key, sseEmitter) -> {
            sseRepository.saveEventCache(key, pheed);
            emitEventToClient(sseEmitter, key, pheed);
        });
    }

    private PheedResponse pheedCreateRequest(String con, String sort, String empCode, String url) {
        Employee employee = employeeRepository.findByEmpCode(Integer.parseInt(empCode));

        return PheedResponse.builder()
                .pheedCon(con)
                .creStatus(LocalDateTime.now())
                .readStatus("N")
                .deStatus("N")
                .pheedSort(sort)
                .empCode(Integer.parseInt(empCode))
                .url(url)
                .build();
    }

    public PheedResponse createDummyPheed(String empCode) {
        return PheedResponse.builder()
                .pheedCon("hi")
                .creStatus(LocalDateTime.now())
                .readStatus("N")
                .deStatus("N")
                .pheedSort("TEST")
                .empCode(Integer.parseInt(empCode))
                .url("test.com")
                .build();
    }

    private void emitEventToClient(SseEmitter sseEmitter, String emitterId, Object data) {
        try {
            send(sseEmitter, emitterId, (PheedResponse) data);
        } catch (Exception e) {
            sseRepository.deleteEmitterById(emitterId);
            throw new RuntimeException("Connection Failed.");
        }
    }

    // 새로운 Pheed가 저장될 때 클라이언트에게 알림을 보내는 메서드
    public void notifyClientsForNewPheed(Pheed pheed) {
        String empCode = String.valueOf(pheed.getEmployee().getEmp_code());
        Map<String, SseEmitter> sseEmitters = PheedController.sseEmitters; // 이 부분은 실제로 동작하도록 수정이 필요할 수 있습니다.

        sseEmitters.forEach((key, sseEmitter) -> {
            PheedResponse pheedResponse = convertToPheedResponse(pheed);
            emitEventToClient(sseEmitter, key, pheedResponse);
        });
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

    // 모든 피드 조회 메서드
    @Transactional
    public List<PheedResponse> getAllPheeds(int empCode) {
        List<Pheed> pheeds = pheedRepository.findByEmployeeEmpCode(empCode); //최신순 정렬

        return pheeds.stream()
                .map(PheedResponse::pheedResponse)
                .collect(Collectors.toList());
    }

    // 피드 DB 생성 메서드
    @Transactional
    public void registPheed(String pheedCon, String pheedSort, String empCode, String url) {

        Employee employee = employeeRepository.findByEmpCode(2024062);

        Pheed pheed = Pheed.builder()
                .pheedCon(pheedCon)
                .creStatus(LocalDateTime.now())
                .readStatus("N")
                .deStatus("N")
                .pheedSort(pheedSort)
                .employee(employee)
                .url(url)
                .build();

        // 먼저 저장
        Pheed savedPheed = pheedRepository.save(pheed); // 저장된 피드 엔티티를 반환받음

        // 피드 등록 완료 후 이벤트 발행
        eventPublisher.publishEvent(new PheedCreatedEvent(this, savedPheed));
        System.out.println("이벤트 발행 ! ! !");
    }

    /* 피드 읽음 */
    @Transactional
    public PheedResponse updateReadStatus(int pheedCode) {
        Optional<Pheed> optionalPheed = Optional.ofNullable(pheedRepository.findByPheedCode(pheedCode));
        Pheed pheed = optionalPheed.orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다. pheedCode: " + pheedCode));

        pheed.updateReadStatus(); // 읽음 상태 업데이트

        return new PheedResponse(pheed); // 업데이트된 피드 정보를 기반으로 응답 DTO 생성
    }

    @Transactional
    /* 피드 삭제 */
    public PheedResponse updateDeStatus(int pheedCode) {
        Optional<Pheed> optionalPheed = Optional.ofNullable(pheedRepository.findByPheedCode(pheedCode));
        Pheed pheed = optionalPheed.orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다. pheedCode: " + pheedCode));

        pheed.updateDeStatus(); // 읽음 상태 업데이트

        return new PheedResponse(pheed); // 업데이트된 피드 정보를 기반으로 응답 DTO 생성
    }
}
