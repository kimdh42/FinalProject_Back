package synergyhubback.calendar.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synergyhubback.calendar.domain.entity.Event;
import synergyhubback.calendar.dto.request.EventCreateRequest;
import synergyhubback.calendar.dto.request.EventUpdateRequest;
import synergyhubback.calendar.dto.response.EventResponse;
import synergyhubback.calendar.service.EventService;
import synergyhubback.auth.util.TokenUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/event/regist")
    public ResponseEntity<EventResponse> createEvent(@RequestHeader("Authorization") String token, @RequestBody EventCreateRequest eventRequest) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            Event event = eventService.createEvent(
                    eventRequest.getTitle(),
                    eventRequest.getEventCon(),
                    eventRequest.getStartDate(),
                    eventRequest.getEndDate(),
                    eventRequest.isAllDay(),
                    eventRequest.getEventGuests(),
                    empCode,
                    eventRequest.getLabelCode()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(event));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/event/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@RequestHeader("Authorization") String token, @PathVariable String eventId, @RequestBody EventUpdateRequest eventRequest) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            Event event = eventService.updateEvent(
                    eventId,
                    eventRequest.getTitle(),
                    eventRequest.getEventCon(),
                    eventRequest.getStartDate(),
                    eventRequest.getEndDate(),
                    eventRequest.isAllDay(),
                    eventRequest.getEventGuests(),
                    empCode,
                    eventRequest.getLabelCode()
            );
            return ResponseEntity.ok(EventResponse.from(event));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getAllEvents(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
            int empCode = Integer.parseInt(tokenEmpCode);

            List<EventResponse> events = eventService.findAllByEmpCode(empCode).stream().map(EventResponse::from).collect(Collectors.toList());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@RequestHeader("Authorization") String token, @PathVariable String eventId) {
        try {
            return eventService.findById(eventId)
                    .map(event -> ResponseEntity.ok(EventResponse.from(event)))
                    .orElseThrow(() -> new RuntimeException("Event not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<Void> deleteEvent(@RequestHeader("Authorization") String token, @PathVariable String eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
