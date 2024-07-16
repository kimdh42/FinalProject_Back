package synergyhubback.message.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import synergyhubback.auth.util.TokenUtils;
import synergyhubback.common.address.domain.dto.AddressSelect;
import synergyhubback.common.address.service.AddressService;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.message.domain.entity.Message;
import synergyhubback.message.domain.entity.Storage;
import synergyhubback.message.dto.request.*;
import synergyhubback.message.dto.response.*;
import synergyhubback.message.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/emp/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final AddressService addressService;

    /* 받은 쪽지 전체 리스트 조회 */
    @GetMapping("/receive")
    public ResponseEntity<List<ReceiveResponse>> getReceiveMessage(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<ReceiveResponse> receiveList = messageService.getReceiveMessage(empCode);

        System.out.println("receiveList : controller : " + receiveList.size());

        return new ResponseEntity<>(receiveList, HttpStatus.OK);
    }

    /* 보낸 쪽지 전체 리스트 조회 */
    @GetMapping("/send")
    public ResponseEntity<List<SendResponse>> getSendMessage (@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<SendResponse> sendList = messageService.getSendMessage(empCode);

        System.out.println("sendList : controller : " + sendList.size());

        return new ResponseEntity<>(sendList, HttpStatus.OK);
    }

    /* 휴지통 전체 리스트 조회 */
    @GetMapping("/bin")
    public ResponseEntity<List<BinResponse>> getBinMessage (@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<BinResponse> binList = messageService.getBinMessage(empCode);

        System.out.println("binList : controller : " + binList.size());

        return new ResponseEntity<>(binList, HttpStatus.OK);
    }

    /* 받은 쪽지 휴지통 업데이트(PATCH) */
    @PutMapping("/receive/{msgCode}/bin")
    public ResponseEntity<Void> RevMsgDel(@PathVariable String msgCode, @RequestBody RevMsgDelRequest request) {
        messageService.RevMsgDel(msgCode, request.getStorCode());

        return ResponseEntity.noContent().build();
    }

    /* 보낸 쪽지 휴지통 업데이트 (PATCH) */
    @PutMapping("/send/{msgCode}/bin")
    public ResponseEntity<Void> sendMsgDel(@PathVariable String msgCode, @RequestBody SendMsgDelRequest request) {

        messageService.SendMsgDel(msgCode, request.getStorCode());

        return ResponseEntity.noContent().build();
    }

    /* 중요 보관함 전체 조회 */
    @GetMapping("/important")
    public ResponseEntity<List<ImpResponse>> getImpMessage (@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<ImpResponse> impList = messageService.getImpMessage(empCode);

        System.out.println("impList : controller : " + impList.size());

        return new ResponseEntity<>(impList, HttpStatus.OK);
    }

    /* 업무 보관함 전체 조회 */
    @GetMapping("/work")
    public ResponseEntity<List<WorkResponse>> getWorkMessage(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<WorkResponse> workList = messageService.getWorkMessage(empCode);

        System.out.println("workList : controller : " + workList);

        return new ResponseEntity<>(workList, HttpStatus.OK);
    }

    /* 임시 저장 전체 조회 */
    @GetMapping("/temp")
    public ResponseEntity<List<TempResponse>> getTempMessage(@RequestHeader("Authorization") String token) {

        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

        List<TempResponse> tempList = messageService.getTempMessage(empCode);

        System.out.println("tempList = " + tempList);

        return new ResponseEntity<>(tempList, HttpStatus.OK);
    }

    /* MS 코드에 따라 Rev Detail 조회 */
    @GetMapping("/receive/{msgCode}")
    public ResponseEntity<ReceiveResponse> getMsgByCode(@PathVariable String msgCode) {

        Message message = messageService.findMsgByMsgCode(msgCode);
        if(message == null) {
            return ResponseEntity.notFound().build();
        }

        ReceiveResponse response = ReceiveResponse.getReceiveMessage(message);
        return ResponseEntity.ok(response);
    }

    /* MS 코드에 따라 Send Detail 조회 */
    @GetMapping("/send/{msgCode}")
    public ResponseEntity<SendResponse> getSendMsgByCode(@PathVariable String msgCode) {

        Message message = messageService.findSendMsgByMsgCode(msgCode);
        if(message == null) {
            return ResponseEntity.notFound().build();
        }

        SendResponse response = SendResponse.getSendMessage(message);
        return ResponseEntity.ok(response);
    }

    /* Message Send (Insert) */
    @PostMapping("/send")
    public ResponseEntity<ResponseMsg> createMessage(@RequestBody CreateMsgRequest request) {

        try {
            messageService.createMessage(
                    request.getMsgTitle(),
                    request.getMsgCon(),
                    request.getMsgStatus(),
                    request.getEmerStatus(),
                    request.getEmpRev(),
                    request.getEmpSend(),
                    request.getRevStor(),
                    request.getSendStor());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMsg(200, "메세지를 전송했습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMsg(500, "서버 오류" + e.getMessage(), null));
        }
    }

    /* Attachment File Insert */
    @PostMapping("/attach")
    public ResponseEntity<ResponseMsg> registAttach(@RequestParam(value = "files", required = false) MultipartFile[] files) {

        try {

            if (files == null) {
                files = new MultipartFile[0];
            }

            messageService.registAttach(files);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMsg(200, "파일 저장 완료", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMsg(500, "서버 오류" + e.getMessage(), null));
        }
    }

    /* Temp Message Create (Insert) */
    @PostMapping( "/create/temp")
    public ResponseEntity<ResponseMsg> createTemp(@RequestBody CreateTempRequest request) {

        try {
            messageService.createTemp(
                    request.getMsgTitle(),
                    request.getMsgCon(),
                    request.getMsgStatus(),
                    request.getEmerStatus(),
                    request.getEmpRev(),
                    request.getEmpSend(),
                    request.getRevStor(),
                    request.getSendStor()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMsg(200, "임시저장 되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMsg(500, "서버 오류" + e.getMessage(), null));
        }
    }

    /* 쪽지 완전 삭제 */
    @DeleteMapping("/bin/{msgCode}")
    public ResponseEntity<Void> deleteMsg(@PathVariable String msgCode){

        try {
            messageService.deleteMsg(msgCode);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /* 쪽지 읽음 처리 */
    @PatchMapping("/{msgCode}/read")
    public ResponseEntity<Void> changeStatusByReadMsg(@PathVariable String msgCode) {

        messageService.changeStatusByReadMsg(msgCode);

        return ResponseEntity.noContent().build();
    }

    /* 쪽지 읽지 않음 처리 */
    @PatchMapping("/{msgCode}/unread")
    public ResponseEntity<Void> changeStatusByUnreadMsg(@PathVariable String msgCode) {

        messageService.changeStatusByUnreadMsg(msgCode);

        return ResponseEntity.noContent().build();
    }

    /* 쪽지에 저장된 파일 찾기 */
    @GetMapping("/findAttach/{msgCode}")
    public ResponseEntity<List<AttachResponse>> findAttachment(@PathVariable String msgCode) {
        List<AttachResponse> attachList = messageService.findAttachment(msgCode);

        return ResponseEntity.ok(attachList);
    }

    /* 쪽지에 저장된 파일 다운로드 */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadMsgAttach(@RequestParam String attachOriginal, @RequestParam String attachSave) {

        Resource file = messageService.downloadMsgAttach(attachSave);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachOriginal + "\"")
                .body(file);
    }

    /* 중요 보관함 이동 처리 */
    @PutMapping("/toImp/{msgCode}")
    public ResponseEntity<Void> moveToImp(@PathVariable String msgCode, @RequestBody MsgToImpRequest request) {

        messageService.moveToImp(msgCode, request.getStorCode());

        return ResponseEntity.noContent().build();
    }

    /* Receive Msg Status 모두 Y로 업데이트 */
    @PutMapping("/receive/updateStatus")
    public ResponseEntity<String> updateRevMsgStatus(@RequestBody List<String> msgCodes) {

        try {
            messageService.updateRevMsgStatus(msgCodes);

            return ResponseEntity.ok("Receive Msg Status Update Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fail to update Rev Msg Status" + e.getMessage());
        }
    }

    @PutMapping("/receive/updateRevStor")
    public ResponseEntity<String> updateAllRevMsgToBin(@RequestBody List<String> msgCodes) {

        try {
            messageService.updateAllRevMsgToBin(msgCodes);

            return ResponseEntity.ok("Receive Msg Status Update Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fail to update Rev Msg Status" + e.getMessage());
        }
    }

    @PutMapping("send/updateSendStor")
    public ResponseEntity<String> updateAllSendMsgToBin(@RequestBody List<String> msgCodes) {

        try {
            messageService.updateAllSendMsgToBin(msgCodes);

            return ResponseEntity.ok("Receive Msg Status Update Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fail to update Rev Msg Status" + e.getMessage());
        }
    }

    /* 회원 차단 등록 */
    @PostMapping("/block")
    public ResponseEntity<ResponseMsg> blockEmp(@RequestBody CreateBlockEmpRequest request) {

        try {
            messageService.blcokEmp(
                    request.getBlkId(),
                    request.getBlkName()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMsg(200, "회원 차단에 성공했습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMsg(500, "서버 오류" + e.getMessage(), null));
        }
    }

    @GetMapping("/{blkId}/{blkName}")
    public ResponseEntity<BlockEmpResponse> getMessageBlock(
            @PathVariable int blkId,
            @PathVariable int blkName
    ) {
        BlockEmpResponse response = messageService.getBlockByBlkIdAndBlkName(blkId, blkName);

        if (response == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/block/address")
    public ResponseEntity<List<AddressSelect>> getAddressSelect(@RequestHeader("Authorization") String token,
                                                                @RequestParam("emp_code") int empCode) {

        String jwtToken = TokenUtils.getToken(token);
        if(jwtToken == null || !TokenUtils.isValidToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<AddressSelect> addresses = addressService.getMsgBlockAddress(empCode);

        return ResponseEntity.ok(addresses);
    }

    @DeleteMapping("/delete/{blkCode}")
    public ResponseEntity<String> deleteBlkEmp(@PathVariable("blkCode") int blkCode) {
        boolean deleted = messageService.deleteBlkEmp(blkCode);

        if (deleted) {
            return new ResponseEntity<>("삭제 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("삭제 실패", HttpStatus.NOT_FOUND);
        }

    }

}
