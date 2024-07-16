package synergyhubback.pheed.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import synergyhubback.attendance.presentation.ResponseMessage;
import synergyhubback.auth.util.TokenUtils;
import synergyhubback.employee.dto.response.MyInfoResponse;
import synergyhubback.employee.service.EmployeeService;
import synergyhubback.pheed.domain.entity.Pheed;
import synergyhubback.pheed.dto.request.PheedCreateRequest;
import synergyhubback.pheed.dto.response.PheedResponse;
import synergyhubback.pheed.service.PheedService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = {"Authorization", "Last-Event-ID"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pheed")
public class PheedController {

    private final PheedService pheedService;
    private final EmployeeService employeeService;
    private final TokenUtils tokenUtils;

    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    /* 구독 기능 */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestHeader("Authorization") String token,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);


        return pheedService.subscribe(tokenEmpCode, lastEventId);
    }

    // sse를 통한 알림 기능을 받을 사용자들을 등록(저장)할 장소
    // 프론트 쪽에서 사용자가 '로그인'을 하면, 해당 사용자를 sseEmitters에 등록되도록 한다.

    /* 피드 생성 */
    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<ResponseMessage> createPheed(@RequestHeader("Authorization") String token,
                                              @RequestBody PheedCreateRequest request) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);

        pheedService.registPheed(
                request.getPheedCon(),
                request.getPheedSort(),
                tokenEmpCode,
                request.getUrl());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage(200, "피드 등록 성공", null));
    }


    /* 피드 조회 */
    @GetMapping("/list")
    public ResponseEntity<ResponseMessage> getAllPheeds(@RequestHeader("Authorization") String token) {

        /* 토큰을 통한 피드 조회 */
        String jwtToken = TokenUtils.getToken(token);
        int userId = Integer.parseInt(TokenUtils.getEmp_Code(jwtToken));
        List<PheedResponse> pheeds = pheedService.getAllPheeds(userId);

        /* 응답 헤더 설정 */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        /* 응답 데이터 설정 */
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("pheeds", pheeds);

        /* 응답 메시지 생성 */
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }

    /* 피드 읽음 */
    @PutMapping("/update-readStatus/{pheedCode}")
    public ResponseEntity<ResponseMessage> readPheed(@PathVariable int pheedCode) {
        PheedResponse pheedResponse = pheedService.updateReadStatus(pheedCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("read update", pheedResponse);

        ResponseMessage responseMessage = new ResponseMessage(200, "피드 읽음 상태 업데이트 성공", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }


    /* 피드 삭제 */
    @PutMapping("/update-deStatus/{pheedCode}")
    public ResponseEntity<ResponseMessage> deletePheed(@PathVariable int pheedCode) {
        PheedResponse pheedResponse = pheedService.updateDeStatus(pheedCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("delete update", pheedResponse);

        ResponseMessage responseMessage = new ResponseMessage(200, "피드 삭제 업데이트 성공", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }





}
