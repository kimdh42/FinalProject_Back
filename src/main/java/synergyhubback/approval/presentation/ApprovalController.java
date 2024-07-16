package synergyhubback.approval.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import synergyhubback.approval.dto.request.BoxRequest;
import synergyhubback.approval.dto.request.DocRegistRequest;
import synergyhubback.approval.dto.request.FormRegistRequest;
import synergyhubback.approval.dto.response.*;
import synergyhubback.approval.service.ApprovalService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping("/formList")
    public ResponseEntity<List<FormListResponse>> findFormList(){
        List<FormListResponse> formList = approvalService.findFormList();
        return ResponseEntity.ok(formList);
    }

    @GetMapping("/formContent")
    public ResponseEntity<FormContentResponse> findFormContent(@RequestParam final int afCode){
        FormContentResponse formContent = approvalService.findFormContent(afCode);
        return ResponseEntity.ok(formContent);
    }

    @GetMapping("/formLine")
    public ResponseEntity<List<FormLineResponse>> findFormLine(@RequestParam final Integer lsCode){
        final List<FormLineResponse> formLine = approvalService.findFormLine(lsCode);
        return ResponseEntity.ok(formLine);
    }
    @GetMapping("/allLine")
    public ResponseEntity<List<FormLineResponse>> findAllLine(){
        final List<FormLineResponse> formLine = approvalService.findAllLine();
        return ResponseEntity.ok(formLine);
    }

    @GetMapping("/formLineEmp")
    public ResponseEntity<List<LineEmpDTO>> findLineEmpList(@RequestParam final String deptCode, @RequestParam final String titleCode, @RequestParam final Integer lsCode){
        final List<LineEmpDTO> lineEmpList = approvalService.findLineEmpList(deptCode, titleCode, lsCode);
        return ResponseEntity.ok(lineEmpList);
    }

    @GetMapping("/sign")
    public ResponseEntity<Resource> getSign(@RequestParam Integer empCode) {
        String signPath = "C:/SynergyHub/Signimgs/" + empCode + ".png";
        Path filePath = Paths.get(signPath);
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    private final Path signRoot = Paths.get("C:/SynergyHub/Signimgs");

    @PatchMapping("/uploadImage")
    public ResponseEntity<Void> uploadImage(@RequestParam Integer empCode, @RequestParam("image") MultipartFile image) {
        try {
            // 디렉토리가 없으면 생성
            if (!Files.exists(signRoot)) {
                Files.createDirectories(signRoot);
            }

            // 파일 확장자 추출
            String originalFilename = image.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 파일 저장
            String filename = empCode + fileExtension;
            Path filePath = signRoot.resolve(filename);

            // 기존 파일이 있으면 덮어쓰기
            Files.write(filePath, image.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // DB에 이미지명 저장
            approvalService.uploadImage(empCode);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


    @PostMapping("/regist")
    public ResponseEntity<Void> regist(@RequestParam("document") String documentJson, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam boolean temporary){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DocRegistRequest docRegistRequest = objectMapper.registerModule(new JavaTimeModule()).readValue(documentJson, DocRegistRequest.class);

            if (files == null) files = new MultipartFile[0];        // files가 null이면 빈 배열로 초기화하여 전달

            approvalService.regist(docRegistRequest, files, temporary);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/send/document")
    public ResponseEntity<List<ReceiveListResponse>> findDocList(@RequestParam final Integer empCode, @RequestParam final String status){
        final List<ReceiveListResponse> docList = approvalService.findDocList(empCode, status);
        return ResponseEntity.ok(docList);
    }

//    @GetMapping("/send/document")
//    public ResponseEntity<PagingResponse> findDocList(@RequestParam(defaultValue = "1") final Integer page,@RequestParam final Integer empCode, @RequestParam final String status){
//        final Page<DocListResponse> docList = approvalService.findDocList(page, empCode, status);
//        final PagingButtonInfo pagingButtonInfo = Paging.getPagingButtonInfo(docList);
//        final PagingResponse pagingResponse = PagingResponse.of(docList.getContent(), pagingButtonInfo);
//
//        return ResponseEntity.ok(pagingResponse);
//    }


    @GetMapping("/viewInfo")
    public ResponseEntity<DocumentResponse> findViewInfo(@RequestParam final String adCode){
        final DocumentResponse viewInfo = approvalService.findViewInfo(adCode);
        return ResponseEntity.ok(viewInfo);
    }

    @GetMapping("/viewLine")
    public ResponseEntity<List<ViewLineResponse>> findViwLineList(@RequestParam final String adCode){
        final List<ViewLineResponse> viewLineList = approvalService.findViwLineList(adCode);
        return ResponseEntity.ok(viewLineList);
    }
    
    @GetMapping("/viewDetail")
    public ResponseEntity<?> findViewDetail(@RequestParam final String adDetail){
        // 정규 표현식을 사용하여 문자열과 숫자 분리
        Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\d+)");
        Matcher matcher = pattern.matcher(adDetail);

        if (matcher.matches()) {
            String textPart = matcher.group(1);     // 문자열 부분

            Object viewDetail = null;
            switch (textPart){
                case "AP": viewDetail = approvalService.findApDetail(adDetail); break;
                case "AE": viewDetail = approvalService.findAeDetail(adDetail); break;
                case "AATT": viewDetail = approvalService.findAattDetail(adDetail); break;
                case "AAPP": viewDetail = approvalService.findAappDetail(adDetail); break;
            }
            return ResponseEntity.ok(viewDetail);
        }

        return ResponseEntity.badRequest().body("Invalid adDetail format");
    }

    @GetMapping("/viewAttach")
    public ResponseEntity<List<AttachmentResponse>> findAttachList(@RequestParam final String adCode){
        final List<AttachmentResponse> attachList = approvalService.findAttachList(adCode);
        return ResponseEntity.ok(attachList);
    }

    @GetMapping("/downloadAttach")
    public ResponseEntity<Resource> downloadAttach(@RequestParam String attachOriginal, @RequestParam String attachSave) {
        Resource file = approvalService.downloadAttach(attachSave);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachOriginal + "\"")
                .body(file);
    }

    @DeleteMapping("/document/delete")
    public ResponseEntity<Void> deleteDocument(@RequestParam final String adCode){
        approvalService.deleteDocument(adCode);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/modifyStatus")
    public ResponseEntity<Void> modifyStatus(@RequestParam final String adCode){
        approvalService.modifyStatus(adCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/receive/document")
    public ResponseEntity<List<ReceiveListResponse>> findReceiveList(@RequestParam final Integer empCode, @RequestParam final String status){
        final List<ReceiveListResponse> docList = approvalService.findReceiveList(empCode, status);
        return ResponseEntity.ok(docList);
    }

    @PatchMapping("/accept")
    public ResponseEntity<Void> acceptDocument(@RequestParam final Integer empCode, @RequestParam final String status, @RequestParam final String adCode){
        approvalService.acceptDocument(empCode, status, adCode);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/return")
    public ResponseEntity<Void> returnDocument(@RequestParam final Integer empCode, @RequestParam final String adCode, @RequestBody final String talReason){
        approvalService.returnDocunet(empCode, adCode, talReason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registForm")
    public ResponseEntity<Void> registForm(@RequestBody final FormRegistRequest formRegistRequest){
        approvalService.registForm(formRegistRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteForm")
    public ResponseEntity<Void> deleteForm(@RequestParam final int afCode){
        approvalService.deleteForm(afCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/modifyForm")
    public ResponseEntity<Void> modifyForm(@RequestBody final FormRegistRequest formRegistRequest, @RequestParam final int afCode){
        approvalService.modifyForm(formRegistRequest, afCode);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/nonActiveForm")
    public ResponseEntity<Void> nonActiveForm(@RequestParam final int afCode){
        approvalService.nonActiveForm(afCode);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activeForm")
    public ResponseEntity<Void> activeForm(@RequestParam final int afCode){
        approvalService.activeForm(afCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/checkIsForm")
    public ResponseEntity<Boolean> checkIsForm(@RequestParam final int afCode){
        final Boolean isForm = approvalService.checkIsForm(afCode);
        return ResponseEntity.ok(isForm);
    }


    @PostMapping("/registBox")
    public ResponseEntity<Void> registBox(@RequestBody final BoxRequest boxRequest){
        approvalService.registBox(boxRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/boxList")
    public ResponseEntity<List<BoxListResponse>> findBoxList(@RequestParam final Integer empCode){
        final List<BoxListResponse> boxList = approvalService.findBoxList(empCode);
        return ResponseEntity.ok(boxList);
    }

    @PatchMapping("/modifybox")
    public ResponseEntity<Void> modifybox(@RequestParam int abCode, @RequestBody BoxRequest boxRequest){
        approvalService.modifybox(abCode, boxRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteBox")
    public ResponseEntity<Void> deleteBox(@RequestParam final int abCode){
        approvalService.deleteBox(abCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registDocInStorage")
    public ResponseEntity<Void> registDocInStorage(@RequestParam String adCode, @RequestParam int abCode){
        approvalService.registDocInStorage(adCode, abCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/docListInStorage")
    public ResponseEntity<List<ReceiveListResponse>> findDocListInStorage(@RequestParam int abCode){
        final List<ReceiveListResponse> docList = approvalService.findDocListInStorage(abCode);
        return ResponseEntity.ok(docList);
    }

    @DeleteMapping("/deleteDocInStorage")
    public ResponseEntity<Void> deleteDocInStorage(@RequestParam String adCode, @RequestParam int abCode){
        approvalService.deleteDocInStorage(adCode, abCode);
        return ResponseEntity.noContent().build();
    }

}
