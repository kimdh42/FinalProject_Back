package synergyhubback.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.approval.domain.entity.TrueLine;
import synergyhubback.calendar.domain.entity.Event;
import synergyhubback.calendar.domain.entity.Guest;
import synergyhubback.calendar.domain.entity.Label;
import synergyhubback.calendar.domain.repository.EventRepository;
import synergyhubback.calendar.domain.repository.GuestRepository;
import synergyhubback.calendar.domain.repository.LabelRepository;
import synergyhubback.common.event.PheedCreatedEvent;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.repository.EmployeeRepository;
import synergyhubback.pheed.domain.entity.Pheed;
import synergyhubback.pheed.domain.repository.PheedRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final EmployeeRepository employeeRepository;
    private final LabelRepository labelRepository;
    private final PheedRepository pheedRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Event createEvent(String title, String eventCon, LocalDateTime startDate, LocalDateTime endDate,
                             boolean allDay, String eventGuests, int empCode, long labelCode) {

        Employee employee = findEmployeeById(empCode);
        Label label = findLabelById(labelCode);

        Event event = Event.createEvent(
                title, eventCon, startDate, endDate, allDay, eventGuests, employee, label
        );

        event.setId(generateEventId()); // 이벤트 ID 생성 로직
        Event savedEvent = eventRepository.save(event);

        // 피드 생성 및 저장 로직 추가
        createAndSendPheed(eventCon, empCode, savedEvent.getId());

        return savedEvent;

    }

    private void createAndSendPheed(String eventCon, int empCode, String eventId) {
        // 이벤트 작성자 정보 가져오기
        String writer = employeeRepository.findEmpNameById(empCode);

        // 피드 내용 작성
        String pheedContent = writer + "님이 '" + eventCon + "' 이벤트를 생성하였습니다.";
        String getUrl = "/event/view/" + eventId;

        // 참석자 목록 가져오기
        List<Guest> guests = guestRepository.findByEventId(eventId);

        for (Guest guest : guests) {
            Employee receiver = guest.getEmployee();

            // 피드 생성
            Pheed newPheed = Pheed.of(
                    pheedContent,
                    LocalDateTime.now(), "N", "N",
                    eventId,
                    receiver,
                    getUrl
            );
            pheedRepository.save(newPheed);
            eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed));
        }
    }


    public Event updateEvent(String eventId, String title, String eventCon, LocalDateTime startDate, LocalDateTime endDate,
                             boolean allDay, String eventGuests, int empCode, long labelCode) {

        Event event = findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        Employee employee = findEmployeeById(empCode);
        Label label = findLabelById(labelCode);

        event.setTitle(title);
        event.setEventCon(eventCon);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setAllDay(allDay);
        event.setEventGuests(eventGuests);
        event.setEmployee(employee);
        event.setLabel(label);

        return eventRepository.save(event);
    }

    public void deleteEvent(String eventId) {
        guestRepository.deleteByEventId(eventId);
        eventRepository.deleteById(eventId);
    }

    public Optional<Event> findById(String eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findAllByEmpCode(int empCode) {
        return eventRepository.findEventsByEmployeeOrGuest(empCode);
    }



    public Employee findEmployeeById(int empCode) {
        return employeeRepository.findById(empCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Label findLabelById(long labelCode) {
        return labelRepository.findById(labelCode)
                .orElseThrow(() -> new RuntimeException("Label not found"));
    }


    private String generateEventId() {
        Optional<Event> lastEventOptional = eventRepository.findTopByOrderByIdDesc();
        if (lastEventOptional.isPresent()) {
            String lastCode = lastEventOptional.get().getId().replaceAll("[^0-9]", "");
            int lastNumber = Integer.parseInt(lastCode);
            return "CA" + (lastNumber + 1);
        } else {
            return "CA1";
        }
    }
}
