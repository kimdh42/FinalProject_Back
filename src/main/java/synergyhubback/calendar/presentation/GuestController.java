package synergyhubback.calendar.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synergyhubback.calendar.domain.entity.Guest;
import synergyhubback.calendar.dto.request.GuestCreateRequest;
import synergyhubback.calendar.dto.request.GuestUpdateRequest;
import synergyhubback.calendar.dto.response.GuestResponse;
import synergyhubback.calendar.service.GuestService;
import synergyhubback.auth.util.TokenUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    public ResponseEntity<List<GuestResponse>> getAllGuests(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

            List<Guest> guests = guestService.findAll();
            List<GuestResponse> response = guests.stream().map(this::convertToResponse).collect(Collectors.toList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{guestCode}")
    public ResponseEntity<GuestResponse> getGuestById(@PathVariable String guestCode, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

            Guest guest = guestService.findById(guestCode).orElse(null);
            if (guest != null) {
                return new ResponseEntity<>(convertToResponse(guest), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<GuestResponse> createGuest(@RequestBody GuestCreateRequest guestCreateRequest, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

            Guest newGuest = guestService.createGuest(guestCreateRequest);
            return new ResponseEntity<>(convertToResponse(newGuest), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{guestCode}")
    public ResponseEntity<GuestResponse> updateGuest(@PathVariable String guestCode, @RequestBody GuestUpdateRequest guestUpdateRequest, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

            Guest updatedGuest = guestService.updateGuest(guestCode, guestUpdateRequest);
            return new ResponseEntity<>(convertToResponse(updatedGuest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{guestCode}")
    public ResponseEntity<HttpStatus> deleteGuest(@PathVariable String guestCode, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

            guestService.deleteGuest(guestCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<GuestResponse>> getGuestsByEventId(@PathVariable String eventId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

            List<Guest> guests = guestService.findByEventId(eventId);
            List<GuestResponse> response = guests.stream().map(this::convertToResponse).collect(Collectors.toList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }



    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<GuestResponse>> getGuestsByTaskId(@PathVariable String taskId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = TokenUtils.getToken(token);
            String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

            List<Guest> guests = guestService.findByTaskId(taskId);
            List<GuestResponse> response = guests.stream().map(this::convertToResponse).collect(Collectors.toList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // Guest 엔티티를 GuestResponse DTO로 변환
    private GuestResponse convertToResponse(Guest guest) {
        GuestResponse response = new GuestResponse();
        response.setGuestCode(guest.getGuestCode());
        response.setEmpCode(guest.getEmployee().getEmp_code());
        response.setEventCode(guest.getEvent() != null ? guest.getEvent().getId() : null);
        response.setTaskCode(guest.getTask() != null ? guest.getTask().getId() : null);
        return response;
    }
}
