package synergyhubback.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import synergyhubback.common.address.domain.dto.AddressSelect;
import synergyhubback.common.attachment.AttachmentEntity;
import synergyhubback.common.attachment.AttachmentRepository;
import synergyhubback.common.event.PheedCreatedEvent;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.entity.Position;
import synergyhubback.employee.domain.entity.Title;
import synergyhubback.message.domain.entity.Message;
import synergyhubback.message.domain.entity.MessageBlock;
import synergyhubback.message.domain.entity.Storage;
import synergyhubback.message.domain.repository.MessageBlockRepository;
import synergyhubback.message.domain.repository.MessageRepository;
import synergyhubback.message.domain.repository.MsgEmpRepository;
import synergyhubback.message.domain.repository.StorageRepository;
import synergyhubback.message.dto.request.CreateBlockEmpRequest;
import synergyhubback.message.dto.response.*;
import synergyhubback.pheed.domain.entity.Pheed;
import synergyhubback.pheed.domain.repository.PheedRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("messageService")
@Transactional
@RequiredArgsConstructor
public class MessageService {

    @Value("${file.message-dir}")
    private String messageDir;

    private final MessageRepository messageRepository;
    private final StorageRepository storageRepository;
    private final AttachmentRepository attachmentRepository;
    private final MessageBlockRepository messageBlockRepository;
    private final PheedRepository pheedRepository;
    private final ApplicationEventPublisher eventPublisher;

    /* emp_code를 찾아서 받은 쪽지 리스트 조회 */
    public List<ReceiveResponse> getReceiveMessage(int empCode) {

        List<Message> receiveList = messageRepository.findByEmpRev_EmpCode(empCode);

        return receiveList.stream()
                .map(ReceiveResponse::getReceiveMessage)
                .collect(Collectors.toList());
    }

    /* emp_code를 찾아서 보낸 쪽지 리스트 조회 */
    public List<SendResponse> getSendMessage(int empCode) {

        List<Message> sendList = messageRepository.findByEmpSend_EmpCode(empCode);

        return sendList.stream()
                .map(SendResponse::getSendMessage)
                .collect(Collectors.toList());
    }

    /* 휴지통 리스트 전체 조회*/
    public List<BinResponse> getBinMessage(int empCode) {

        List<Message> binList = messageRepository.findByBin_EmpCode(empCode);

        return binList.stream()
                .map(BinResponse::getBinMessage)
                .collect(Collectors.toList());
    }

    /* 받은 쪽지 휴지통 PUT 업데이트 로직 */
    @Transactional
    public void RevMsgDel(String msgCode, int storCode) {
        Message message = messageRepository.findById(msgCode)
                .orElseThrow(() -> new IllegalArgumentException("message not found with msgCode : " + msgCode));

        Storage storage = storageRepository.findById(storCode)
                .orElseThrow(() -> new IllegalArgumentException("storage not found with storCode : " + storCode ));

        message.setRevStor(storage);

        messageRepository.save(message);
    }

    /* 보낸 쪽지 휴지통 PUT 업데이트 로직 */
    @Transactional
    public void SendMsgDel(String msgCode, int storCode) {
        Message message = messageRepository.findById(msgCode)
                .orElseThrow(() -> new IllegalArgumentException("message not found with msgCode : " + msgCode));

        Storage storage = storageRepository.findById(storCode)
                .orElseThrow(() -> new IllegalArgumentException("storage not found with storCode : " + storCode));

        message.setSendStor(storage);

        messageRepository.save(message);
    }

    /* 중요 보관함 전체 조회 로직 */
    public List<ImpResponse> getImpMessage(int empCode) {

        List <Message> impList = messageRepository.findByImp_EmpCode(empCode);

        return impList.stream()
                .map(ImpResponse::getImpMessage)
                .collect(Collectors.toList());
    }

    /* 업무 보관함 전체 조회 로직 */
    public List<WorkResponse> getWorkMessage(int empCode) {

        List<Message> workList = messageRepository.findByWork_EmpCode(empCode);

        return workList.stream()
                .map(WorkResponse::getWorkMessage)
                .collect(Collectors.toList());
    }
    public List<TempResponse> getTempMessage(int empCode) {

        List<Message> tempList = messageRepository.findByTemp_empCode(empCode);

        return tempList.stream()
                .map(TempResponse::getTempMessage)
                .collect(Collectors.toList());
    }

    /* Rev Msg By MsgCode */
    public Message findMsgByMsgCode(String msgCode) {
        return messageRepository.findByMsgCode(msgCode);
    }

    /* Send Msg By MsgCode */
    public Message findSendMsgByMsgCode(String msgCode) {
        return messageRepository.findSendMsgByMsgCode(msgCode);
    }

    /* old msgCode를 가져와서 비교 후 new msgCode로 교체 */
    private String newMsgCode(String lastMsgCode) {

        if (lastMsgCode == null || !lastMsgCode.matches("\\d+")) {
            return "MS1";
        }

        int lastNum = Integer.parseInt(lastMsgCode);

        return "MS" + (lastNum + 1);
    }

    /* Send Msg (Insert) */
    @Transactional
    public void createMessage(String msgTitle, String msgCon, String msgStatus, String emerStatus, Employee empRev, Employee empSend, Storage revStor, Storage sendStor) {

        String lastMsgCode = messageRepository.findLastMsgCode();
        String newMsgCode = newMsgCode(lastMsgCode);

        Message message = Message.create(
                newMsgCode,
                LocalDateTime.now(),
                msgTitle,
                msgCon,
                msgStatus,
                emerStatus
        );

        message.setEmpRev(empRev);
        message.setEmpSend(empSend);
        message.setRevStor(revStor);
        message.setSendStor(sendStor);

        messageRepository.save(message);

        String getEmpName = messageRepository.findEmpNameByMsgCode(newMsgCode);

        String pheedContent = getEmpName + "님이 쪽지를 발송하였습니다.";
        String getUrl = "/message/storage/receive/detail/" + newMsgCode;

        Pheed newPheed = Pheed.of(
                pheedContent,
                LocalDateTime.now(), "N", "N",
                newMsgCode,
                message.getEmpRev(),
                getUrl
        );

        pheedRepository.save(newPheed);
        eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed));

    }


    // UUID로 아이디 저장
    private String uniqueSaveFileName(String originalFileName) {

        String uuid = UUID.randomUUID().toString();
        String extension = "";

        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) extension = originalFileName.substring(dotIndex);

        return uuid + extension;
    }

    /* Temp Create Msg */
    public void createTemp(String msgTitle, String msgCon, String msgStatus, String emerStatus, Employee empRev, Employee empSend, Storage revStor ,Storage sendStor) {
        String lastMsgCode = messageRepository.findLastMsgCode();
        String newMsgCode = newMsgCode(lastMsgCode);

        Message message = Message.create(
                newMsgCode,
                LocalDateTime.now(),
                msgTitle,
                msgCon,
                msgStatus,
                emerStatus
        );

        message.setEmpRev(empRev);
        message.setEmpSend(empSend);
        message.setRevStor(revStor);
        message.setSendStor(sendStor);

        messageRepository.save(message);
    }

    @Transactional
    public void deleteMsg(String msgCode) {

        if(messageRepository.existsById(msgCode)) {
            messageRepository.deleteById(msgCode);
        } else {
            throw new IllegalArgumentException("msgCode가 없음 : " + msgCode);
        }
    }


    public void changeStatusByReadMsg(String msgCode) {

        Optional<Message> optional = messageRepository.findById(msgCode);

        if (optional.isPresent()) {

            Message message = optional.get();
            message.setMsgStatus("Y");  // 읽음 상태로 변경
            messageRepository.save(message);

        } else {
            throw new IllegalArgumentException("쪽지를 찾을 수 없습니다. " + msgCode);
        }
    }

    public void changeStatusByUnreadMsg(String msgCode) {

        Optional<Message> optional = messageRepository.findById(msgCode);

        if (optional.isPresent()) {

            Message message = optional.get();
            message.setMsgStatus("N");  // 읽지 않음 상태로 변경
            messageRepository.save(message);

        } else  {
            throw new IllegalArgumentException("쪽지를 찾을 수 없습니다. " + msgCode);
        }
    }

    /* 파일 저장 API */
    public void registAttach(MultipartFile[] files) {

        /* 가장 최근에 저장한 msgCode 가져오기 */
        Message message = messageRepository.findByRecentMsg();

        /* 파일 저장 */
        File uploadDir = new File(messageDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {

            String originalFileName = file.getOriginalFilename();
            String saveFileName = uniqueSaveFileName(originalFileName);

            File destFile = new File(messageDir + File.separator + saveFileName);

            try {
                file.transferTo(destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            AttachmentEntity attachment = AttachmentEntity.create(
                    originalFileName,
                    saveFileName,
                    messageDir,
                    message.getMsgCode()
            );

            attachmentRepository.save(attachment);
        }

    }

    /* 쪽지에 저장된 파일 찾기 */
    public List<AttachResponse> findAttachment(String msgCode) {
        List<AttachmentEntity> attachList = attachmentRepository.findByMsgCode(msgCode);

        return attachList.stream().map(AttachResponse::find).toList();
    }

    /* 파일 저장 로직 */
    public Resource downloadMsgAttach(String attachSave) {
        try {
            Path filePath = Paths.get(messageDir).resolve(attachSave).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + attachSave);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found : " + attachSave, e);
        }
    }

    @Transactional
    public void moveToImp(String msgCode, int storCode) {

        Message message = messageRepository.findById(msgCode)
                .orElseThrow(() -> new IllegalArgumentException("message not found with msgCode : " + msgCode));

        Storage storage = storageRepository.findById(storCode)
                .orElseThrow(() -> new IllegalArgumentException("storage not found with storCode : " + storCode));

        message.setRevStor(storage);

        messageRepository.save(message);
    }

    /* Rev Msg Status 전체 Y 업데이트 */
    @Transactional
    public void updateRevMsgStatus(List<String> msgCodes) {

        List<Message> msgToUpdate = messageRepository.findByMsgCodeIn(msgCodes);
        msgToUpdate.forEach(msg -> msg.setMsgStatus("Y"));

        messageRepository.saveAll(msgToUpdate);
    }


    public void updateAllRevMsgToBin(List<String> msgCodes) {

        List<Message> revMsgToUpdate = messageRepository.findByMsgCodeIn(msgCodes);

        revMsgToUpdate.forEach(msg -> {

            Storage storage = new Storage();
            storage.setStorCode(5);
            msg.setRevStor(storage);

            messageRepository.save(msg);
        });
    }

    public void updateAllSendMsgToBin(List<String> msgCodes) {

        List<Message> revMsgToUpdate = messageRepository.findByMsgCodeIn(msgCodes);

        revMsgToUpdate.forEach(msg -> {

            Storage storage = new Storage();
            storage.setStorCode(5);
            msg.setSendStor(storage);

            messageRepository.save(msg);
        });
    }

    public void blcokEmp(Employee blkId, Employee blkName) {

        Integer lastBlkCode = messageBlockRepository.findLastBlkCode();
        if(lastBlkCode == null) {
            lastBlkCode = 1;
        }
        int newBlkCode = newBlkCode(lastBlkCode);

        MessageBlock messageBlock = MessageBlock.create(
                newBlkCode,
                LocalDate.now()
        );

        messageBlock.setBlkId(blkId);
        messageBlock.setBlkName(blkName);

        messageBlockRepository.save(messageBlock);
    }

    private int newBlkCode(Integer lastBlkCode) {

        if (lastBlkCode != null && lastBlkCode > 0) {

            return lastBlkCode + 1;
        } else if (lastBlkCode == null) {

            lastBlkCode = 0;

            return lastBlkCode;
        } else {

            throw new IllegalArgumentException("lastBlkCode는 음수일 수 없습니다." + lastBlkCode);
        }
    }

    public BlockEmpResponse getBlockByBlkIdAndBlkName(int blkId, int blkName) {

        MessageBlock messageBlock = messageBlockRepository.findByBlkIdAndBlkName(blkId, blkName);

        if (messageBlock == null) {

            return null;
        }

        return BlockEmpResponse.getBlockEmp(messageBlock);
    }

    public boolean deleteBlkEmp(int blkCode) {

        try {
            messageBlockRepository.deleteById(blkCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
