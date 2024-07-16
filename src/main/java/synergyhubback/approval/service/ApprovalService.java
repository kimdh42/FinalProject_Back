package synergyhubback.approval.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import synergyhubback.approval.dao.LineEmpMapper;
import synergyhubback.approval.domain.entity.*;
import synergyhubback.approval.domain.repository.*;
import synergyhubback.approval.dto.request.BoxRequest;
import synergyhubback.approval.dto.request.DocRegistRequest;
import synergyhubback.approval.dto.request.FormRegistRequest;
import synergyhubback.approval.dto.response.*;
import synergyhubback.attendance.domain.entity.Attendance;
import synergyhubback.attendance.domain.entity.AttendanceStatus;
import synergyhubback.attendance.domain.repository.AttendanceRepository;
import synergyhubback.attendance.domain.repository.AttendanceStatusRepository;
import synergyhubback.common.attachment.AttachmentEntity;
import synergyhubback.common.attachment.AttachmentRepository;
import synergyhubback.common.event.ApprovalCompletedEvent;
import synergyhubback.common.event.PheedCreatedEvent;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.repository.EmployeeRepository;
import synergyhubback.pheed.domain.entity.Pheed;
import synergyhubback.pheed.domain.repository.PheedRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalService {

    private final EmployeeRepository employeeRepository;
    @Value("${file.approval-dir}")
    private String approvalDir;

    private final FormRepository formRepository;
    private final LineRepository lineRepository;
    private final LineEmpMapper lineEmpMapper;
    private final DocRepository docRepository;
    private final EtcRepository etcRepository;
    private final PersonalRepository personalRepository;
    private final ApprovalAttendanceRepository approvalAttendanceRepository;
    private final ApprovalAppointRepository approvalAppointRepository;
    private final AppointDetailRepository appointDetailRepository;
    private final ApprovalBoxRepository approvalBoxRepository;
    private final ApprovalStorageRepository approvalStorageRepository;
    private final TrueLineRepository trueLineRepository;
    private final AttachmentRepository attachmentRepository;
    private final PheedRepository pheedRepository;
    private final LineSortRepository lineSortRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;

    /* 박은비 추가 */
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<FormListResponse> findFormList() {
        List<Form> formList = formRepository.findAll();
        return formList.stream().map(FormListResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FormContentResponse findFormContent(final int afCode) {
        Form formContent = formRepository.findById(afCode).orElse(null);

        if (formContent == null) {
            throw new RuntimeException("Form not found for afCode: " + afCode);
        }

        return FormContentResponse.from(formContent);
    }

    @Transactional(readOnly = true)
    public List<FormLineResponse> findFormLine(final Integer lsCode) {
        List<Line> formLine = lineRepository.findByLineSortLsCodeOrderByAlOrderAsc(lsCode);
        return formLine.stream().map(FormLineResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FormLineResponse> findAllLine() {
        List<Line> formLine = lineRepository.findAll();
        return formLine.stream().map(FormLineResponse::from).toList();
    }

    public List<LineEmpDTO> findLineEmpList(final String deptCode, final String titleCode, final Integer lsCode) {
        return lineEmpMapper.findLineEmpList(deptCode, titleCode, lsCode);
    }

    @Transactional
    public void regist(DocRegistRequest docRegistRequest, MultipartFile[] files, @RequestParam boolean temporary) {
        String adCode = docRegistRequest.getAdCode();
        System.out.println("adCode = " + adCode);
        String adDetail = docRegistRequest.getAdDetail();

        if (adCode != null && !adCode.isEmpty()) {
            Document existDoc = docRepository.findById(adCode).orElseThrow(() -> new IllegalArgumentException("Invalid adCode:" + adCode));

            if (existDoc != null){
                // 결재문서 수정 후, 저장
                existDoc.modifyDocument(docRegistRequest.getAdTitle(),docRegistRequest.getAdReportDate(),docRegistRequest.getAdStatus());
                docRepository.save(existDoc);

                // 결재상세 수정 후, 저장
                Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\d+)");
                Matcher matcher = pattern.matcher(adDetail);

                if (matcher.matches()) {
                    String textPart = matcher.group(1);
                    switch (textPart){
                        case "AP":
                            Personal existPersonal = personalRepository.findById(adDetail).orElseThrow(() -> new IllegalArgumentException("Invalid adDetail:" + adDetail));
                            existPersonal.modifyPersonal(
                                    docRegistRequest.getPersonal().getApStart(),
                                    docRegistRequest.getPersonal().getApEnd(),
                                    docRegistRequest.getPersonal().getApContact(),
                                    docRegistRequest.getPersonal().getApReason()
                            );
                            personalRepository.save(existPersonal); break;

                        case "AE":
                            Etc existEtc = etcRepository.findById(adDetail).orElseThrow(() -> new IllegalArgumentException("Invalid adDetail:" + adDetail));
                            existEtc.modifyEtc(docRegistRequest.getEtc().getAeCon());
                            etcRepository.save(existEtc); break;

                        case "AATT":
                            ApprovalAttendance existAttend = approvalAttendanceRepository.findById(adDetail).orElseThrow(() -> new IllegalArgumentException("Invalid adDetail:" + adDetail));
                            existAttend.modifyApprovalAttendance(
                                    docRegistRequest.getApprovalAttendance().getAattSort(),
                                    docRegistRequest.getApprovalAttendance().getAattStart(),
                                    docRegistRequest.getApprovalAttendance().getAattEnd(),
                                    docRegistRequest.getApprovalAttendance().getAattOccur(),
                                    docRegistRequest.getApprovalAttendance().getAattPlace(),
                                    docRegistRequest.getApprovalAttendance().getAattCon(),
                                    docRegistRequest.getApprovalAttendance().getAattReason()
                            );
                            approvalAttendanceRepository.save(existAttend); break;

                        case "AAPP":
                            ApprovalAppoint existAppoint = approvalAppointRepository.findById(adDetail).orElseThrow(() -> new IllegalArgumentException("Invalid adDetail:" + adDetail));
                            existAppoint.modifyApprovalAppoint(
                                    docRegistRequest.getApprovalAppoint().getAappNo(),
                                    docRegistRequest.getApprovalAppoint().getAappDate(),
                                    docRegistRequest.getApprovalAppoint().getAappTitle()
                            );
                            approvalAppointRepository.save(existAppoint);

                            List<AppointDetail> existDetailList = appointDetailRepository.findByApprovalAppoint_AappCode(adDetail);
                            List<AppointDetail> newDetailList = docRegistRequest.getAppointDetailList();
                            int existSize = existDetailList.size();
                            int newSize = newDetailList.size();

                            // 길이가 같으면 그대로 덮어쓰기
                            if (existSize == newSize) {
                                IntStream.range(0, existSize).forEach(i -> {
                                    AppointDetail existList = existDetailList.get(i);
                                    AppointDetail newList = newDetailList.get(i);
                                    updateAppointDetail(existList, newList);
                                });
                            }
                            // existTrueLine이 더 길면 순서대로 덮어쓰고 남은 부분 삭제
                            else if (existSize > newSize) {
                                IntStream.range(0, newSize).forEach(i -> {
                                    AppointDetail existList = existDetailList.get(i);
                                    AppointDetail newList = newDetailList.get(i);
                                    updateAppointDetail(existList, newList);
                                });
                                // 남은 뒷부분 삭제
                                IntStream.range(newSize, existSize).forEach(i -> {
                                    appointDetailRepository.delete(existDetailList.get(i));
                                });
                            }
                            // newTrueLine이 더 길면 순서대로 덮어쓰고 나머지 값 추가
                            else {
                                IntStream.range(0, existSize).forEach(i -> {
                                    AppointDetail existList = existDetailList.get(i);
                                    AppointDetail newList = newDetailList.get(i);
                                    updateAppointDetail(existList, newList);
                                });
                                // 나머지 값 추가
                                IntStream.range(existSize, newSize).forEach(i -> {
                                    AppointDetail newList = newDetailList.get(i);
                                    appointDetailRepository.save(newList);
                                });
                            }

                    }
                }

                // 실결재라인 수정 후, 저장
                List<TrueLine> existTrueLine = trueLineRepository.findByDocument_AdCode(adCode);
                List<TrueLine> newTrueLine = docRegistRequest.getTrueLineList();
                int existSize = existTrueLine.size();
                int newSize = newTrueLine.size();

                // 길이가 같으면 그대로 덮어쓰기
                if (existSize == newSize) {
                    IntStream.range(0, existSize).forEach(i -> {
                        TrueLine existLine = existTrueLine.get(i);
                        TrueLine newLine = newTrueLine.get(i);
                        updateTrueLine(existLine, newLine);
                    });
                }
                // existTrueLine이 더 길면 순서대로 덮어쓰고 남은 부분 삭제
                else if (existSize > newSize) {
                    IntStream.range(0, newSize).forEach(i -> {
                        TrueLine existLine = existTrueLine.get(i);
                        TrueLine newLine = newTrueLine.get(i);
                        updateTrueLine(existLine, newLine);
                    });
                    // 남은 뒷부분 삭제
                    IntStream.range(newSize, existSize).forEach(i -> {
                        trueLineRepository.delete(existTrueLine.get(i));
                    });
                }
                // newTrueLine이 더 길면 순서대로 덮어쓰고 나머지 값 추가
                else {
                    IntStream.range(0, existSize).forEach(i -> {
                        TrueLine existLine = existTrueLine.get(i);
                        TrueLine newLine = newTrueLine.get(i);
                        updateTrueLine(existLine, newLine);
                    });
                    // 나머지 값 추가
                    Document foundDoc = docRepository.findById(adCode).orElseThrow(() -> new IllegalArgumentException("Invalid Document code:" + adCode));
                    IntStream.range(existSize, newSize).forEach(i -> {
                        TrueLine newLine = TrueLine.of(
                                foundDoc,
                                newTrueLine.get(i).getTalOrder(),
                                newTrueLine.get(i).getTalRole(),
                                "미결재",
                                newTrueLine.get(i).getEmployee()
                        );
                        trueLineRepository.save(newLine);
                    });
                }

                // 첨부파일 수정 후, 저장
                List<AttachmentEntity> attachList = attachmentRepository.findByAttachSort(adCode);

                // 일단 원래 첨부파일 전부 삭제하고
                if(attachList != null && !attachList.isEmpty()){
                    attachmentRepository.deleteAll(attachList);         // db에서 삭제

                    for (AttachmentEntity attachment : attachList) {    // 로컬 폴더에서 파일삭제
                        String attachSave = attachment.getAttachSave();
                        try {
                            Path filePath = Paths.get(attachment.getAttachUrl(), attachSave);
                            Files.delete(filePath);
                        } catch (Exception e) {
                            System.err.println("Failed to delete file: " + attachSave);
                            e.printStackTrace();
                        }
                    }
                }

                // 새로 받아온 첨부파일 다시 저장
                File uploadDirectory = new File(approvalDir);   // 업로드 디렉토리가 존재하지 않으면 생성합니다.
                if (!uploadDirectory.exists()) uploadDirectory.mkdirs();

                for (MultipartFile file : files) {
                    String originalFileName = file.getOriginalFilename();
                    String saveFileName = generateSaveFileName(originalFileName);

                    // 파일 저장 경로 생성
                    File destFile = new File(approvalDir + File.separator + saveFileName);

                    // 파일을 로컬에 저장
                    try {
                        file.transferTo(destFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 첨부파일 테이블에 정보 저장
                    AttachmentEntity newAttachment = AttachmentEntity.of(
                            originalFileName,
                            saveFileName,
                            approvalDir,
                            adCode
                    );
                    attachmentRepository.save(newAttachment);
                }

                // 임시저장 문서를 상신할 경우, 임시저장 정보 삭제
                if(temporary == false){
                    int abCode = 1; // 임시저장
                    approvalStorageRepository.deleteByDocument_AdCodeAndApprovalBox_AbCode(adCode, abCode);

                    // 피드
                    int writerCode = docRepository.findEmployeeEmpCodeById(adCode); // 결재문서코드로 작성자 empCode 조회
                    String writer = employeeRepository.findEmpNameById(writerCode); // 작성자 이름 조회
                    String ttl = docRepository.findAdTitleById(adCode);             // 결재문서 제목 조회

                    List<TrueLine> filteredTrueLineList = docRegistRequest.getTrueLineList().stream().filter(line -> line.getTalOrder() != 0).toList();
                    Employee receiver = filteredTrueLineList.get(0).getEmployee();  // 결재라인에서 첫번째 결재자 받아오기

                    String pheedContent = writer + "님이 '" + ttl + "' 결재를 상신하였습니다.";
                    String getUrl = "/approval/receive/waiting";

                    // 첫번째 결재자한테 피드 보내기
                    Pheed newPheed = Pheed.of(
                            pheedContent,
                            LocalDateTime.now(), "N", "N",
                            adCode,
                            receiver,
                            getUrl
                    );
                    pheedRepository.save(newPheed);
                    eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed));

                    // 모든 참조자한테 피드 보내기
                    List<TrueLine> referTrueLineList = docRegistRequest.getTrueLineList().stream().filter(line -> line.getTalRole().equals("참조")).toList();
                    for(TrueLine line : referTrueLineList){
                        Employee refer = line.getEmployee();
                        String referName = employeeRepository.findEmpNameById(refer.getEmp_code());

                        String pheedContent1 = writer + "님이 '" + ttl + "' 결재를 상신하였습니다. " + referName + "님은 '참조자'로써 해당 결재를 확인하실 수 있습니다.";
                        String getUrl1 = "/approval/receive/reference";

                        Pheed newPheed1 = Pheed.of(
                                pheedContent1,
                                LocalDateTime.now(), "N", "N",
                                adCode,
                                refer,
                                getUrl1
                        );
                        pheedRepository.save(newPheed1);
                        eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed1));
                    }
                }
            }
        } else {

            int afCode = docRegistRequest.getForm().getAfCode();
            String code = "";

            // 결재양식별 결재상세내용 해당 테이블에 저장
            if (afCode == 2 || afCode == 3 || afCode == 4 || afCode == 5) {
                code = generateAttendanceCode();
                ApprovalAttendance newAttendance = null;

                if (afCode == 2) {            // 예외근무신청서
                    newAttendance = ApprovalAttendance.of(code,
                            docRegistRequest.getApprovalAttendance().getAattSort(),
                            docRegistRequest.getApprovalAttendance().getAattStart(),
                            docRegistRequest.getApprovalAttendance().getAattEnd(),
                            docRegistRequest.getApprovalAttendance().getAattPlace(),
                            docRegistRequest.getApprovalAttendance().getAattCon()
                    );
                } else if (afCode == 3) {      // 초과근무신청서
                    newAttendance = ApprovalAttendance.of(code,
                            docRegistRequest.getApprovalAttendance().getAattReason(),
                            docRegistRequest.getApprovalAttendance().getAattSort(),
                            docRegistRequest.getApprovalAttendance().getAattStart(),
                            docRegistRequest.getApprovalAttendance().getAattEnd(),
                            docRegistRequest.getApprovalAttendance().getAattPlace(),
                            docRegistRequest.getApprovalAttendance().getAattCon()
                    );
                } else if (afCode == 4) {      // 지각사유서
                    newAttendance = ApprovalAttendance.of(code,
                            docRegistRequest.getApprovalAttendance().getAattSort(),
                            docRegistRequest.getApprovalAttendance().getAattOccur(),
                            docRegistRequest.getApprovalAttendance().getAattReason()
                    );
                } else if (afCode == 5) {      // 휴가신청서
                    newAttendance = ApprovalAttendance.of(code,
                            docRegistRequest.getApprovalAttendance().getAattSort(),
                            docRegistRequest.getApprovalAttendance().getAattStart(),
                            docRegistRequest.getApprovalAttendance().getAattEnd()
                    );
                }

                approvalAttendanceRepository.save(newAttendance);
            } else if (afCode == 7 || afCode == 8) {        // 휴직신청서 & 사직신청서
                code = generatePersonalCode();
                Personal newPersonal = Personal.of(code,
                        docRegistRequest.getPersonal().getApStart(),
                        docRegistRequest.getPersonal().getApEnd(),
                        docRegistRequest.getPersonal().getApContact(),
                        docRegistRequest.getPersonal().getApReason()
                );
                personalRepository.save(newPersonal);
            } else if (afCode == 1) {   // 인사발령
                code = generateAppointCode();
                ApprovalAppoint newAppoint = ApprovalAppoint.of(code,
                        docRegistRequest.getApprovalAppoint().getAappNo(),
                        docRegistRequest.getApprovalAppoint().getAappDate(),
                        docRegistRequest.getApprovalAppoint().getAappTitle()
                );
                approvalAppointRepository.save(newAppoint);

                List<AppointDetail> AppointDetailList = docRegistRequest.getAppointDetailList();
                for (AppointDetail detail : AppointDetailList) {
                    Employee employee = employeeRepository.findByEmpCode(detail.getEmployee().getEmp_code());

                    AppointDetail newDetail = AppointDetail.of(
                            newAppoint,
                            detail.getAdetBefore(),
                            detail.getAdetAfter(),
                            detail.getAdetType(),
                            employee
                    );
                    appointDetailRepository.save(newDetail);
                }
            } else {    // 기타결재
                code = generateEtcCode();
                Etc newEtc = Etc.of(code, docRegistRequest.getEtc().getAeCon());
                etcRepository.save(newEtc);
            }

            // 결재문서 저장
            String documentCode = generateDocumentCode();
            Document newDoc = Document.of(
                    documentCode,
                    docRegistRequest.getAdTitle(),
                    docRegistRequest.getEmployee(),
                    docRegistRequest.getAdReportDate(),
                    docRegistRequest.getAdStatus(),
                    docRegistRequest.getForm(),
                    code
            );
            docRepository.save(newDoc);

            // 방금 저장한 결재문서 조회해오기
            Document findDoc = docRepository.findByAdDetail(code);

            // 실결재라인 저장
            List<TrueLine> TrueLineList = docRegistRequest.getTrueLineList();
            for (TrueLine line : TrueLineList) {
                TrueLine newLine = TrueLine.of(
                        findDoc,
                        line.getTalOrder(),
                        line.getTalRole(),
                        "미결재",
                        line.getEmployee()
                );
                trueLineRepository.save(newLine);
            }

            // 첨부파일 저장
            File uploadDirectory = new File(approvalDir);   // 업로드 디렉토리가 존재하지 않으면 생성합니다.
            if (!uploadDirectory.exists()) uploadDirectory.mkdirs();

            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();
                String saveFileName = generateSaveFileName(originalFileName);

                // 파일 저장 경로 생성
                File destFile = new File(approvalDir + File.separator + saveFileName);

                // 파일을 로컬에 저장
                try {
                    file.transferTo(destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 첨부파일 테이블에 정보 저장
                AttachmentEntity newAttachment = AttachmentEntity.of(
                        originalFileName,
                        saveFileName,
                        approvalDir,
                        findDoc.getAdCode()
                );
                attachmentRepository.save(newAttachment);
            }

            if(!temporary){     // 임시저장이 아닐때만 즉, 결재상신일때만 피드에 정보 저장
                // 작성자 이름 받아오기
                int writerCode = docRegistRequest.getEmployee().getEmp_code();
                String writer = employeeRepository.findEmpNameById(writerCode);

                // 결재문서 제목 받아오기
                String ttl = docRepository.findAdTitleById(documentCode);

                // 결재라인에서 첫번째 결재자 받아오기
                List<TrueLine> filteredTrueLineList = TrueLineList.stream().filter(line -> line.getTalOrder() != 0).toList();
                Employee receiver = filteredTrueLineList.get(0).getEmployee();

                String pheedContent = writer + "님이 '" + ttl + "' 결재를 상신하였습니다.";
                String getUrl = "/approval/receive/waiting";

                // 첫번째 결재자한테 피드 보내기
                Pheed newPheed = Pheed.of(
                        pheedContent,
                        LocalDateTime.now(), "N", "N",
                        documentCode,
                        receiver,
                        getUrl
                );
                pheedRepository.save(newPheed);
                eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed));



                // 모든 참조자한테 피드 보내기
                List<TrueLine> referTrueLineList = TrueLineList.stream().filter(line -> line.getTalRole().equals("참조")).toList();
                for(TrueLine line : referTrueLineList){
                    Employee refer = line.getEmployee();
                    String referName = employeeRepository.findEmpNameById(refer.getEmp_code());

                    String pheedContent1 = writer + "님이 '" + ttl + "' 결재를 상신하였습니다. " + referName + "님은 '참조자'로써 해당 결재를 확인하실 수 있습니다.";
                    String getUrl1 = "/approval/receive/reference";

                    Pheed newPheed1 = Pheed.of(
                            pheedContent1,
                            LocalDateTime.now(), "N", "N",
                            documentCode,
                            refer,
                            getUrl1
                    );
                    pheedRepository.save(newPheed1);
                    eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed1));
                }
            }

            if (temporary) {  // 임시저장
                String finalCode = code;
                ApprovalBox findBox = approvalBoxRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Invalid ApprovalBox code:" + finalCode));

                ApprovalStorage newStorage = ApprovalStorage.of(findDoc, findBox);
                approvalStorageRepository.save(newStorage);
            }

        }
    }

    private void updateAppointDetail(AppointDetail existList, AppointDetail newList){
        existList.modifyAppointDetail(
                newList.getAdetBefore(),
                newList.getAdetAfter(),
                newList.getAdetType(),
                newList.getEmployee()
        );
        appointDetailRepository.save(existList);
    }

    private void updateTrueLine(TrueLine existLine, TrueLine newLine) {
        existLine.modifyTrueLine(
                newLine.getTalOrder(),
                newLine.getTalRole(),
                "미결재",
                newLine.getEmployee(),
                newLine.getTalReason(),
                newLine.getTalDate()
        );
        trueLineRepository.save(existLine);
    }

    private String generatePersonalCode() {
        Optional<Personal> lastOptional = personalRepository.findTopOrderByApCodeDesc();
        return generateCode(lastOptional, "AP");
    }

    private String generateEtcCode() {
        Optional<Etc> lastOptional = etcRepository.findTopOrderByAeCodeDesc();
        return generateCode(lastOptional, "AE");
    }

    private String generateAttendanceCode(){
        Optional<ApprovalAttendance> lastOptional = approvalAttendanceRepository.findTopOrderByAattCodeDesc();
        return generateCode(lastOptional, "AATT");
    }

    private String generateAppointCode(){
        Optional<ApprovalAppoint> lastOptional = approvalAppointRepository.findTopOrderByAappCodeDesc();
        return generateCode(lastOptional, "AAPP");
    }

    private String generateDocumentCode(){
        Optional<Document> lastOptional = docRepository.findTopOrderByAdCodeDesc();
        return generateCode(lastOptional, "AD");
    }

    private String generateCode(Optional<?> lastOptional, String prefix) {
        if (lastOptional.isPresent()) {
            Object lastCodeObject = lastOptional.get();
            String lastCodeStr = "";

            if (lastCodeObject instanceof Personal) {
                lastCodeStr = ((Personal) lastCodeObject).getApCode();
            } else if (lastCodeObject instanceof Etc) {
                lastCodeStr = ((Etc) lastCodeObject).getAeCode();
            } else if (lastCodeObject instanceof ApprovalAttendance) {
                lastCodeStr = ((ApprovalAttendance) lastCodeObject).getAattCode();
            } else if (lastCodeObject instanceof ApprovalAppoint) {
                lastCodeStr = ((ApprovalAppoint) lastCodeObject).getAappCode();
            } else if (lastCodeObject instanceof Document) {
                lastCodeStr = ((Document) lastCodeObject).getAdCode();
            } else if (lastCodeObject instanceof String) {
                lastCodeStr = (String) lastCodeObject;
            } else {
                throw new IllegalArgumentException("Unsupported entity type");
            }

            String numericPart = lastCodeStr.replaceAll("[^\\d]", "");
            String nonNumericPart = lastCodeStr.replaceAll("[\\d]", "");

            int numericValue = Integer.parseInt(numericPart) + 1;

            return nonNumericPart + numericValue;
        } else {
            return prefix + "1";
        }
    }

    private String generateSaveFileName(String originalFileName) {
        // UUID를 사용하여 고유한 파일명 생성
        String uuid = UUID.randomUUID().toString();
        String extension = "";

        // 확장자가 있으면 분리했다가 다시 합쳐서 반환
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) extension = originalFileName.substring(dotIndex);

        return uuid + extension;
    }

    private Pageable getPageable(final Integer page){
        return PageRequest.of(page - 1, 10);
    }

    @Transactional(readOnly = true)
    public List<ReceiveListResponse> findDocList(final Integer empCode, String status) {
        switch (status){
            case "temporary": status = "임시저장"; break;
            case "waiting" : status = "대기"; break;
            case "progress" : status = "진행중"; break;
            case "return" : status  = "반려"; break;
            case "complete" : status = "완료"; break;
        }

        List<TrueLine> docList = null;

        if(status.equals("완료")){
            docList = trueLineRepository.findCompleteSendList(empCode);
        }else if(status.equals("반려")){
            docList = trueLineRepository.findReturnSendList(empCode);
        }else{
            docList = trueLineRepository.findWaitingSendList(empCode, status);
        }

        return docList.stream().map(ReceiveListResponse::from).toList();
    }

//    public Page<DocListResponse> findDocList(final Integer page, final Integer empCode, String status) {
//        switch (status){
//            case "waiting" : status = "대기"; break;
//            case "process" : status = "진행중"; break;
//            case "return" : status  = "반려"; break;
//            case "complete" : status = "완료"; break;
//        }
//        Page<TrueLine> docList = trueLineRepository.findTrueLineWithPendingStatus(getPageable(page), empCode, status);
//
//        return docList.map(DocListResponse::from);
//    }


    public DocumentResponse findViewInfo(String adCode) {
        Document viewInfo = docRepository.findById(adCode).orElseThrow(() -> new IllegalArgumentException("Invalid adCode:" + adCode));
        return DocumentResponse.from(viewInfo);
    }

    public List<ViewLineResponse> findViwLineList(final String adCode) {
        List<TrueLine> viewLineList = trueLineRepository.findViewLineList(adCode);
        return viewLineList.stream().map(ViewLineResponse::from).toList();
    }

    public ApResponse findApDetail(final String adDetail) {
        Personal viewDetail = personalRepository.findById(adDetail).orElseThrow(() -> new IllegalArgumentException("Invalid adDetail:" + adDetail));
        return ApResponse.from(viewDetail);
    }

    public AeResponse findAeDetail(final String adDetail) {
        Etc viewDetail = etcRepository.findById(adDetail).orElseThrow(() -> new IllegalArgumentException("Invalid adDetail:" + adDetail));
        return AeResponse.from(viewDetail);
    }

    public AattResponse findAattDetail(final String adDetail) {
        ApprovalAttendance viewDetail = approvalAttendanceRepository.findById(adDetail).orElseThrow(() -> new IllegalArgumentException("Invalid adDetail:" + adDetail));
        return AattResponse.from(viewDetail);
    }

    public List<AappResponse> findAappDetail(final String adDetail) {
        // 대한님 출력내용 확인 후, reponse 구성 수정필요
        List<AppointDetail> viewDetail = appointDetailRepository.findByAdDetail(adDetail);
        return viewDetail.stream().map(AappResponse::from).toList();
    }

    public List<AttachmentResponse> findAttachList(final String adCode) {
        List<AttachmentEntity> attachList = attachmentRepository.findByAttachSort(adCode);
        return attachList.stream().map(AttachmentResponse::from).toList();
    }

    public Resource downloadAttach(final String attachSave) {
        try {
            Path filePath = Paths.get(approvalDir).resolve(attachSave).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + attachSave);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found " + attachSave, e);
        }
    }

    public void deleteDocument(final String adCode) {
        // 첨부파일 삭제
        List<AttachmentEntity> attachList = attachmentRepository.findByAttachSort(adCode);
        if(attachList != null && !attachList.isEmpty()){
            attachmentRepository.deleteAll(attachList);         // db에서 삭제

            for (AttachmentEntity attachment : attachList) {    // 로컬 폴더에서 파일삭제
                String attachSave = attachment.getAttachSave();
                try {
                    Path filePath = Paths.get(attachment.getAttachUrl(), attachSave);
                    Files.delete(filePath);
                } catch (Exception e) {
                    System.err.println("Failed to delete file: " + attachSave);
                    e.printStackTrace();
                }
            }
        }

        // 상세내용 삭제
        String adDetail = docRepository.findAdDetailById(adCode);

        Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\d+)");
        Matcher matcher = pattern.matcher(adDetail);

        if (matcher.matches()) {
            String textPart = matcher.group(1);
            switch (textPart){
                case "AP": personalRepository.deleteById(adDetail); break;
                case "AE": etcRepository.deleteById(adDetail); break;
                case "AATT": approvalAttendanceRepository.deleteById(adDetail); break;
                case "AAPP": approvalAppointRepository.deleteById(adDetail); break;
            }
        }

        // 결재보관내역 삭제
        List<ApprovalStorage> storageList = approvalStorageRepository.findByDocument_AdCode(adCode);
        if(storageList != null && !storageList.isEmpty()) approvalStorageRepository.deleteByDocument_AdCode(adCode);

        // 실결재라인 삭제
        trueLineRepository.deleteByDocument_AdCode(adCode);

        // 결재문서 삭제
        docRepository.deleteById(adCode);
    }

    @Transactional
    public void modifyStatus(final String adCode) {     // 상신했던 결재문서 수정하기
        Document foundDocument = docRepository.findById(adCode).orElseThrow(() -> new RuntimeException("Document not found with adCode: " + adCode));

        // 결재문서 상태 변경
        foundDocument.modifyAdStatus("임시저장");
        docRepository.save(foundDocument);

        // 임시저장 시킴
        ApprovalBox findBox = approvalBoxRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Invalid ApprovalBox code:" + 1));
        ApprovalStorage newStorage = ApprovalStorage.of(foundDocument, findBox);
        approvalStorageRepository.save(newStorage);

        // 해당 문서코드로 저장된 피드정보가 아직은 하나밖에 없을테니까
        pheedRepository.deleteByPheedSort(adCode);
    }

    public List<ReceiveListResponse> findReceiveList(Integer empCode, String status) {
        List<TrueLine> docList = null;

        if(status.equals("complete")){
            docList = trueLineRepository.findCompleteReceiveList(empCode);
        }else if(status.equals("return")){
            docList = trueLineRepository.findReturnReceiveList(empCode);
        }else if(status.equals("reference")){
            docList = trueLineRepository.findReferenceReceiveList(empCode);
        }else{
            docList = trueLineRepository.findWaitingReceiveList(empCode);
        }

        return docList.stream().map(ReceiveListResponse::from).toList();
    }

    public void acceptDocument(Integer empCode, String status, String adCode) {
        // 결재 시작 피드 전송을 위해서 가장 먼저 해당 문서의 상태 조회
        String foundAdStatus = docRepository.findAdStatusById(adCode);

        Document foundDocument = docRepository.findById(adCode).orElseThrow(() -> new RuntimeException("Document not found with adCode: " + adCode));

        if(status.equals("전결")) {
            foundDocument.modifyStatus("완료");

            // 이후 순서 전결처리
            List<TrueLine> afterList = trueLineRepository.findAfterList(empCode, adCode);
            for (TrueLine line : afterList) {
                line.modifyAccept("전결", LocalDate.now());
                trueLineRepository.save(line);
            }
        }else{
            foundDocument.modifyStatus(status);     // status : 진행중, 완료
        }
        docRepository.save(foundDocument);

        TrueLine foundLine = trueLineRepository.findByEmployee_Emp_codeAndDocument_AdCode(empCode, adCode);
        foundLine.modifyAccept("승인", LocalDate.now());
        trueLineRepository.save(foundLine);

        if(foundDocument.getAdDetail().equals("2") || foundDocument.getAdDetail().equals("3") || foundDocument.getAdDetail().equals("5")) {

            // 예외근무, 초과근무, 휴가근무 관련 문서라면 결재 완료 이벤트 발행
            eventPublisher.publishEvent(new ApprovalCompletedEvent(adCode));
        }

        System.out.println("결재 완료 이벤트 발행");

        // 피드
        String approver = employeeRepository.findEmpNameById(empCode);  // 승인한 사람 이름 조회
        String ttl = docRepository.findAdTitleById(adCode);             // 결재문서 제목 조회

        int writerCode = docRepository.findEmployeeEmpCodeById(adCode);         // 작성자 사원코드 조회
        String writerName = employeeRepository.findEmpNameById(writerCode);     // 작성자 이름 조회
        Employee writer = employeeRepository.findById(writerCode).orElseThrow(() -> new RuntimeException("Employee not found with empCode: " + writerCode));

        List<TrueLine> findTrueLineList = trueLineRepository.findByDocument_AdCode(adCode);
        List<TrueLine> readTrueLineList = findTrueLineList.stream().filter(line -> line.getTalRole().equals("열람")).toList();

        if(foundAdStatus.equals("대기")){                         // 결재가 시작되었을때 전송
            // 작성자한테 보내는 피드
            String pheedContent1 = "'" + ttl + "' 결재가 시작되었습니다.";
            String getUrl = "/approval/send/progress";
            Pheed newPheed1 = Pheed.of(
                    pheedContent1,
                    LocalDateTime.now(), "N", "N",
                    adCode,
                    writer,
                    getUrl
            );
            pheedRepository.save(newPheed1);
            eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed1));

            // 피드 등록 완료 후 이벤트 발행
            eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed1));
        }

        String foundAdStatus2 = docRepository.findAdStatusById(adCode); // 결재 승인 후에 문서 상태 조회
        if(foundAdStatus2.equals("완료")){     // 결재가 완료되었을때 전송
            // 작성자한테 보내는 피드
            String pheedContent2 = "'" + ttl + "' 결재가 완료되었습니다.";
            String getUrl = "/approval/send/complete";
            Pheed newPheed2 = Pheed.of(
                    pheedContent2,
                    LocalDateTime.now(), "N", "N",
                    adCode,
                    writer,
                    getUrl
            );
            pheedRepository.save(newPheed2);
            eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed2));

            // 열람자한테 보내는 피드
            for(TrueLine line: readTrueLineList){
                Employee reader = line.getEmployee();
                String readerName = employeeRepository.findEmpNameById(reader.getEmp_code());

                String pheedContent0 = writerName + "님의 '" + ttl + "' 결재가 완료되었습니다. " + readerName + "님은 '열람자'로써 해당 결재를 확인하실 수 있습니다.";
                String getUrl1 = "/approval/receive/reference";

                Pheed newPheed0 = Pheed.of(
                        pheedContent0,
                        LocalDateTime.now(), "N", "N",
                        adCode,
                        reader,
                        getUrl1
                );
                pheedRepository.save(newPheed0);
                eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed0));
            }

        }

        // 다음 결재자한테 보내기
        List<TrueLine> afterList = trueLineRepository.findAfterList(empCode, adCode);   // 다음 결재자 리스트 조회
        if (afterList != null && !afterList.isEmpty()) {
            Employee nextApprover = afterList.get(0).getEmployee();                // 다음 결재자 중 첫번째 결재자 조회

            String pheedContent3 = writerName + "님이 상신한 '" + ttl + "' 결재가 대기중입니다.";
            String getUrl = "/approval/receive/waiting";
            Pheed newPheed3 = Pheed.of(
                    pheedContent3,
                    LocalDateTime.now(), "N", "N",
                    adCode,
                    nextApprover,
                    getUrl
            );
            pheedRepository.save(newPheed3);
            eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed3));
        }
    }

    public void returnDocunet(Integer empCode, String adCode, String talReason) {
        Document foundDocument = docRepository.findById(adCode).orElseThrow(() -> new RuntimeException("Document not found with adCode: " + adCode));
        foundDocument.modifyStatus("반려");
        docRepository.save(foundDocument);

        TrueLine foundLine = trueLineRepository.findByEmployee_Emp_codeAndDocument_AdCode(empCode, adCode);
        foundLine.modifyReturn("반려", talReason, LocalDate.now());
        trueLineRepository.save(foundLine);

        // 피드
        String turnedEmployee = employeeRepository.findEmpNameById(empCode);    // 반려한 사람 이름 조회
        String ttl = docRepository.findAdTitleById(adCode);                     // 결재문서 제목 조회

        int writerCode = docRepository.findEmployeeEmpCodeById(adCode);         // 작성자 사원코드 조회
        Employee receiver = employeeRepository.findById(writerCode).orElseThrow(() -> new RuntimeException("Employee not found with empCode: " + writerCode));

        String pheedContent = turnedEmployee + "님이 '" + ttl + "' 결재를 반려하였습니다.";
        String getUrl = "/approval/send/return";

        Pheed newPheed = Pheed.of(
                pheedContent,
                LocalDateTime.now(), "N", "N",
                adCode,
                receiver,
                getUrl
        );
        pheedRepository.save(newPheed);
        eventPublisher.publishEvent(new PheedCreatedEvent(this, newPheed));
    }

    public void registForm(FormRegistRequest formRegistRequest) {
        int lsCode = formRegistRequest.getLineSort().getLsCode();
        LineSort foundLine = lineSortRepository.findById(lsCode).orElseThrow(() -> new RuntimeException("LineSort not found with lsCode: " + lsCode));


        Form newForm = Form.of(
                formRegistRequest.getAfName(),
                formRegistRequest.getAfExplain(),
                foundLine,
                formRegistRequest.getAfCon(),
                'Y'
        );
        formRepository.save(newForm);
    }

    public void deleteForm(int afCode) {
        formRepository.deleteById(afCode);
    }

    public void modifyForm(FormRegistRequest formRegistRequest, int afCode) {
        int lsCode = formRegistRequest.getLineSort().getLsCode();
        LineSort foundLine = lineSortRepository.findById(lsCode).orElseThrow(() -> new RuntimeException("LineSort not found with lsCode: " + lsCode));

        Form foundForm = formRepository.findById(afCode).orElseThrow(() -> new RuntimeException("Form not found with afCode: " + afCode));

        foundForm.modifyForm(
                formRegistRequest.getAfName(),
                formRegistRequest.getAfExplain(),
                foundLine,
                formRegistRequest.getAfCon()
        );
        formRepository.save(foundForm);
    }

    public void nonActiveForm(int afCode) {
        Form foundForm = formRepository.findById(afCode).orElseThrow(() -> new RuntimeException("Form not found with afCode: " + afCode));
        foundForm.nonActiveForm('N');
        formRepository.save(foundForm);
    }

    public void activeForm(int afCode) {
        Form foundForm = formRepository.findById(afCode).orElseThrow(() -> new RuntimeException("Form not found with afCode: " + afCode));
        foundForm.nonActiveForm('Y');
        formRepository.save(foundForm);
    }

    public Boolean checkIsForm(int afCode) {
        Boolean isForm = docRepository.existsByForm_AfCode(afCode);
        return isForm;
    }


    public void registBox(BoxRequest boxRequest) {
        ApprovalBox newBox = ApprovalBox.of(
                boxRequest.getAbName(),
                boxRequest.getEmpCode()
        );
        approvalBoxRepository.save(newBox);
    }

    @Transactional(readOnly = true)
    public List<BoxListResponse> findBoxList(Integer empCode) {
        List<ApprovalBox> boxList = approvalBoxRepository.findByEmpCode(empCode);
        return boxList.stream().map(BoxListResponse::from).toList();
    }

    public void modifybox(int abCode, BoxRequest boxRequest) {
        ApprovalBox foundBox = approvalBoxRepository.findById(abCode).orElseThrow(() -> new RuntimeException("ApprovalBox not found with abCode: " + abCode));
        foundBox.modifyName(boxRequest.getAbName());
        approvalBoxRepository.save(foundBox);
    }

    public void deleteBox(int abCode) {
        approvalStorageRepository.deleteByApprovalBox_AbCode(abCode);
        approvalBoxRepository.deleteById(abCode);
    }

    public void registDocInStorage(String adCode, int abCode) {
        Document foundDoc = docRepository.findById(adCode).orElseThrow(() -> new RuntimeException("Document not found with adCode: " + adCode));
        ApprovalBox foundBox = approvalBoxRepository.findById(abCode).orElseThrow(() -> new RuntimeException("ApprovalBox not found with abCode: " + abCode));

        List<ApprovalStorage> foundStorage = approvalStorageRepository.findByDocument_AdCodeAndApprovalBox_AbCode(adCode, abCode);

        if(foundStorage == null || foundStorage.isEmpty()){
            ApprovalStorage newStorage = ApprovalStorage.of(foundDoc, foundBox);
            approvalStorageRepository.save(newStorage);
        }else{
            throw new RuntimeException("Document with adCode " + adCode + " is already stored in ApprovalBox with abCode " + abCode);
        }
    }

    public List<ReceiveListResponse> findDocListInStorage(int abCode) {
        List<TrueLine> docList = trueLineRepository.findDocListInStorage(abCode);
        return docList.stream().map(ReceiveListResponse::from).toList();
    }

    public void deleteDocInStorage(String adCode, int abCode) {
        approvalStorageRepository.deleteByDocument_AdCodeAndApprovalBox_AbCode(adCode, abCode);
    }

    public void uploadImage(Integer empCode) {
        Employee foundEmployee = employeeRepository.findById(empCode).orElseThrow(() -> new RuntimeException("Employee not found with empCode: " + empCode));
        foundEmployee.signRegist(empCode);
        employeeRepository.save(foundEmployee);
    }

}
