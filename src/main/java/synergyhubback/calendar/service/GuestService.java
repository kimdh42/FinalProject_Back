package synergyhubback.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.calendar.domain.entity.Guest;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.calendar.domain.entity.Event;
import synergyhubback.calendar.domain.entity.Task;
import synergyhubback.calendar.domain.repository.GuestRepository;
import synergyhubback.employee.domain.repository.EmployeeRepository;
import synergyhubback.calendar.domain.repository.EventRepository;
import synergyhubback.calendar.domain.repository.TaskRepository;
import synergyhubback.calendar.dto.request.GuestCreateRequest;
import synergyhubback.calendar.dto.request.GuestUpdateRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;
    private final EmployeeRepository employeeRepository;
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;

    public Guest createGuest(GuestCreateRequest guestCreateRequest) {
        Guest guest = convertToEntity(guestCreateRequest);
        guest.setGuestCode(generateGuestCode());
        return guestRepository.save(guest);
    }

    public Guest updateGuest(String guestCode, GuestUpdateRequest guestUpdateRequest) {
        Guest guest = findById(guestCode).orElseThrow(() -> new RuntimeException("Guest not found"));
        guest.setEmployee(findEmployeeById(guestUpdateRequest.getEmpCode()));
        guest.setEvent(guestUpdateRequest.getEventCode() != null ? findEventById(guestUpdateRequest.getEventCode()) : null);
        guest.setTask(guestUpdateRequest.getTaskCode() != null ? findTaskById(guestUpdateRequest.getTaskCode()) : null);
        return guestRepository.save(guest);
    }

    public void deleteGuest(String guestCode) {
        guestRepository.deleteById(guestCode);
    }

    public Optional<Guest> findById(String guestCode) {
        return guestRepository.findById(guestCode);
    }


    public List<Guest> findAll() {
        return guestRepository.findAll();
    }

    private Employee findEmployeeById(int empCode) {
        return employeeRepository.findById(empCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private Event findEventById(String eventCode) {
        return eventRepository.findById(eventCode)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    private Task findTaskById(String taskCode) {
        return taskRepository.findById(taskCode)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<Guest> findByEventId(String eventId) {
        return guestRepository.findByEventId(eventId);
    }

    public List<Guest> findByTaskId(String taskId) {
        return guestRepository.findByTaskId(taskId);
    }



    private String generateGuestCode() {
        Optional<Guest> lastGuestOptional = guestRepository.findTopByOrderByGuestCodeDesc();
        if (lastGuestOptional.isPresent()) {
            String lastCode = lastGuestOptional.get().getGuestCode().replaceAll("[^0-9]", "");
            int lastNumber = Integer.parseInt(lastCode);
            return "GU" + (lastNumber + 1);
        } else {
            return "GU1";
        }
    }

    // GuestCreateRequest DTO를 Guest 엔티티로 변환
    private Guest convertToEntity(GuestCreateRequest guestCreateRequest) {
        Guest guest = new Guest();
        guest.setEmployee(findEmployeeById(guestCreateRequest.getEmpCode()));
        guest.setEvent(guestCreateRequest.getEventCode() != null ? findEventById(guestCreateRequest.getEventCode()) : null);
        guest.setTask(guestCreateRequest.getTaskCode() != null ? findTaskById(guestCreateRequest.getTaskCode()) : null);
        return guest;
    }

    // GuestUpdateRequest DTO를 Guest 엔티티로 변환
    private Guest convertToEntity(GuestUpdateRequest guestUpdateRequest) {
        Guest guest = new Guest();
        guest.setEmployee(findEmployeeById(guestUpdateRequest.getEmpCode()));
        guest.setEvent(guestUpdateRequest.getEventCode() != null ? findEventById(guestUpdateRequest.getEventCode()) : null);
        guest.setTask(guestUpdateRequest.getTaskCode() != null ? findTaskById(guestUpdateRequest.getTaskCode()) : null);
        return guest;
    }
}
