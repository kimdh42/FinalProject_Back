package synergyhubback.post.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import synergyhubback.auth.util.TokenUtils;
import synergyhubback.common.attachment.AttachmentEntity;
import synergyhubback.post.domain.entity.*;
import synergyhubback.post.domain.type.PostCommSet;
import synergyhubback.post.dto.request.*;
import synergyhubback.post.dto.response.CommonResponse;
import synergyhubback.post.dto.response.LowBoardResponse;
import synergyhubback.post.dto.response.PostResponse;
import synergyhubback.post.dto.response.PostRoleResponse;
import synergyhubback.post.service.PostService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    @Value("${image.post-image-dir}")
    private String POST_FILE_DIR;

    private final PostService postService;

    @GetMapping("/getRoll/{PostCode}/{Roll}")
    public ResponseEntity<List<PostRollRequest>> getRoll(@PathVariable("PostCode") int LowBoardCode, @PathVariable("Roll") String roll) {
        System.out.println(roll);
        return ResponseEntity.ok(postService.getRollRequests(LowBoardCode, roll));
    }

    @PostMapping("/boardCreate")
    public ResponseEntity<LowBoardEntity> boardCreate(@RequestBody BoardRequest boardRequest) {
        System.out.println("boardRequest : " + boardRequest);

        return ResponseEntity.ok(postService.boardCreate(boardRequest));
    }
    @PutMapping("/boardUpdate/{lowCode}")
    public ResponseEntity<Integer> boardUpdate(@RequestBody BoardRequest boardRequest, @PathVariable("lowCode") int lowCode) {
        System.out.println("boardRequest : " + boardRequest);
        System.out.println("lowCode : " + lowCode);
        return ResponseEntity.ok(postService.boardUpdate(boardRequest, lowCode));
    }
    @PutMapping("/boardDelete/{lowCode}")
    public ResponseEntity<Integer> boardDelete(@PathVariable("lowCode") int lowCode) {
        System.out.println("lowCode : " + lowCode);
        return ResponseEntity.ok(postService.boardDelete(lowCode));
    }
    @GetMapping("/PostRole")
    public ResponseEntity<List<PostRollRequest>> getPostRole() {
        return ResponseEntity.ok(postService.getPostRole());
    }

    @PostMapping("/PostRoleUpdate/{lowBoard}")
    public ResponseEntity<String> postRoleUpdate(@RequestBody List<PostRoleRequest> updatedRoles, @PathVariable("lowBoard") int lowCode) {
        return ResponseEntity.ok(postService.updateRoles(updatedRoles, lowCode));
    }

    @PostMapping("/PostRoleCreate")
    public ResponseEntity<String> postRoleCreate(@RequestBody List<PostRoleRequest> updatedRoles) {
//        PostRoleRequest readPostRoleRequest = payload.get("readPostRoleRequest");
//        PostRoleRequest writePostRoleRequest = payload.get("writePostRoleRequest");
//        PostRoleRequest adminPostRoleRequest = payload.get("adminPostRoleRequest");
//
//        System.out.println("readPostRoleRequest : " + readPostRoleRequest);
//        System.out.println("writePostRoleRequest : " + writePostRoleRequest);
//        System.out.println("adminPostRoleRequest : " + adminPostRoleRequest);

        try {
            postService.createRoles(updatedRoles);
            return ResponseEntity.ok("Roles created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating roles: " + e.getMessage());
        }
    }
    @GetMapping("/ReadyPost/{empCode}")
    public ResponseEntity<List<PostRequest>> ReadyPost(@PathVariable("empCode") int empCode) {
        System.out.println("임시저장 기동");
        return ResponseEntity.ok(postService.ReadyPost(empCode));
    }
    @PutMapping("/postDelete/{postCode}")
    public ResponseEntity<Integer> postDelete(@PathVariable String postCode) {
        System.out.println("postCode : " + postCode);
        return ResponseEntity.ok(postService.postDelete(postCode));
    }

    @GetMapping("/postSearch/{searchWord}")
    public ResponseEntity<List<PostEntity>> postSearch(@PathVariable String searchWord) {
        return ResponseEntity.ok(postService.postSearch(searchWord));
    }

    @PutMapping("/postUpdate/{postCode}")
    public ResponseEntity<Integer> postUpdate(@RequestParam(value = "attachFile", required = false) List<MultipartFile> attachFile,
                                              @RequestParam("postName") String postName,
                                              @RequestParam("postCon") String postCon,
                                              @RequestParam("lowBoardCode") int lowBoardCode,
                                              @RequestParam(value = "postCommSet", defaultValue = "4", required = false) int postCommSet,
                                              @RequestParam(value = "fixStatus", defaultValue = "N", required = false) char fixStatus,
                                              @RequestParam(value = "noticeStatus", defaultValue = "N", required = false) char noticeStatus,
                                              @RequestParam("psCode") int psCode,
                                              @PathVariable String postCode) {
        postService.FileUpdate(attachFile, postCode);
        String fileUploadDir = POST_FILE_DIR;
        PostCommSet commSet = PostCommSet.fromValue(postCommSet);


        File dir1 = new File(fileUploadDir);

        /* 디렉토리가 없을 경우 생성한다. */
        if (!dir1.exists()) {
            dir1.mkdirs();
        }
        if (attachFile != null) {
            // 처리 로직

            List<AttachmentEntity> FileList = new ArrayList<>();

            try {
                for (int i = 0; i < attachFile.size(); i++) {
                    /* 첨부파일이 실제로 존재하는 경우 로직 수행 */
                    if (attachFile.get(i).getSize() > 0) {

                        String originalFileName = attachFile.get(i).getOriginalFilename();

                        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                        String saveFileName = UUID.randomUUID() + ext;

                        /* 서버의 설정 디렉토리에 파일 저장하기 */
                        attachFile.get(i).transferTo(new File(fileUploadDir + "/" + saveFileName));

                        /* DB에 저장할 파일의 정보 처리 */
                        AttachmentEntity fileInfo = new AttachmentEntity(originalFileName, saveFileName, fileUploadDir + "/" + saveFileName, postCode);

                        /* 리스트에 파일 정보 저장 */
                        FileList.add(fileInfo);
                    }

                }
                /* 파일 리스트를 한 번에 DB에 저장 */
                postService.registFileList(FileList);


            } catch (IOException e) {
                /* 파일 저장 중간에 실패 시, 이전에 저장된 파일 삭제 */

            }

        } else {

            /* 업로드 파일에 대한 정보를 담을 리스트 */

        }


        return ResponseEntity.ok(postService.postUpdate(postCode, postName, postCon, lowBoardCode, postCommSet, fixStatus, noticeStatus, psCode));
    }

    @PostMapping("/commentAdd")
    public ResponseEntity<CommontRequest> commentAdd(
            @RequestHeader("Authorization") String token, @RequestBody CommontRequest commontRequest) {


        System.out.println(commontRequest.getPostCode());
        String jwtToken = TokenUtils.getToken(token);
        String tokenEmpCode = TokenUtils.getEmp_Code(jwtToken);
        int empCode = Integer.parseInt(tokenEmpCode);

//        PostEntity postEntity = postService.findByPostCode(commontRequest.getPostCode());
//        if (postEntity == null) {
//            throw new EntityNotFoundException("Post not found");
//        }


        CommontRequest commentEntity = new CommontRequest();
        commentEntity.setCommCon(commontRequest.getCommCon());
        commentEntity.setCommDate(commontRequest.getCommDate());
        commentEntity.setCommStatus(commontRequest.getCommStatus());
        commentEntity.setPostCode(commontRequest.getPostCode());
        System.out.println(empCode);
//        Employee employee = employeeRepository.findByEmpCode(empCode);
//        if (employee == null) {
//            throw new EntityNotFoundException("Employee not found");
//        }

        commentEntity.setEmp_code(empCode);
        return ResponseEntity.ok(postService.commentAdd(token, commontRequest));
    }

    @GetMapping("/commentList/{postCode}")
    public ResponseEntity<List<CommonResponse>> getCommentList(@PathVariable String postCode) {
        return ResponseEntity.ok(postService.getCommentList(postCode));
    }
    @PutMapping("/commentEdit/{commCode}")
    public ResponseEntity<Integer> commentEdit(@PathVariable String commCode, @RequestBody CommontRequest commontRequest) {
        return ResponseEntity.ok(postService.commentEdit(commCode, commontRequest));
    }
    @PutMapping("/commentDelete/{commCode}")
    public ResponseEntity<Integer> commentDelete(@PathVariable String commCode) {
        return ResponseEntity.ok(postService.commentDelete(commCode));
    }

    @GetMapping("/downloadFile/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(POST_FILE_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }


    @DeleteMapping("/deleteComment/{commCode}")
    public ResponseEntity<Integer> deleteComment(@PathVariable String commCode) {
        return ResponseEntity.ok(postService.deleteComment(commCode));
    }

    @PutMapping("/updateComment/{commCode}")
    public ResponseEntity<Integer> updateComment(@PathVariable String commCode, @RequestParam String commCon) {
        System.out.println(commCon);
        return ResponseEntity.ok(postService.updateComment(commCode, commCon));
    }

@GetMapping("/callGETLowBoardListToCode/{lowBoardCode}")
public ResponseEntity<LowBoardResponse> callGETLowBoardListToCode(@PathVariable("lowBoardCode") String lowBoardCode) {
    System.out.println("callGETLowBoardListToCode stared");
    LowBoardEntity lowBoardEntity= postService.callGETLowBoardListToCode(lowBoardCode);
    LowBoardResponse lowBoardResponse = new LowBoardResponse();
    lowBoardResponse.setBoardCode(lowBoardEntity.getBoardCode());
    lowBoardResponse.setLowBoardName(lowBoardEntity.getLowBoardName());
    lowBoardResponse.setLowBoardCode(lowBoardEntity.getLowBoardCode());
    return ResponseEntity.ok(lowBoardResponse);
}
    @GetMapping("/getDetail/{postCode}")
    public ResponseEntity<PostResponse> callGETDetail(@PathVariable("postCode") String postCode) {
        System.out.println("callGETDetail stared");
        postService.ViewCountUp(postCode);
        PostResponse post = postService.getDetail(postCode);
        System.out.println(post);
        post.setPostCommSet(PostCommSet.fromValue(post.getPostCommSet().getValue())); // 이 부분 확인
        return ResponseEntity.ok(post);
    }

    @GetMapping("/callGETFile/{postCode}")
    public ResponseEntity<List<AttachmentEntity>> callGETFile(@PathVariable("postCode") String postCode) {
        System.out.println("callGETFile stared");
        List<AttachmentEntity> fileList = postService.getFile(postCode);
        System.out.println("callGETFile" + fileList);
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/callGETInboardList/{lowBoardCode}")
    public ResponseEntity<List<PostResponse>> callGETInboardList(Pageable pageable, @PathVariable("lowBoardCode") Integer lowBoardCode) {
        System.out.println("callGETInboardList stared");
        List<PostResponse> posts = postService.InboardList(pageable, lowBoardCode);
        System.out.println(posts);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/callGETInboardPinList/{lowBoardCode}")
    public ResponseEntity<List<PostResponse>> callGETInboardPinList(@PageableDefault Pageable pageable, @PathVariable("lowBoardCode") Integer boardCode) {
        System.out.println("callGETInboardPinList stared");
        List<PostResponse> posts = postService.InboardPinList(pageable, boardCode);
        System.out.println(posts);
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/list")
    public ResponseEntity<List<PostResponse>> findPostList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        System.out.println("getList stared");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.ASC, "PostCode");
        System.out.println(pageable);
        List<PostResponse> posts = postService.getAllPostList(pageable);
        System.out.println(posts);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/sortList")
    public ResponseEntity<List<PostSortEntity>> findPostSortList() {
        List<PostSortEntity> postSortEntities = postService.getAllPostSortList();
        return ResponseEntity.ok(postSortEntities);
    }

    @GetMapping("/getAllBoard")
    public ResponseEntity<List<BoardEntity>> getAllBoard() {
        System.out.println("getAllBoard stared");
        List<BoardEntity> boardList = postService.getAllBoard();
        System.out.println("getAllBoard" + boardList);
        return ResponseEntity.ok(boardList);
    }

    @GetMapping("/getLowBoard/{boardCode}")
    public ResponseEntity<List<LowBoardEntity>> getLowBoard(@PathVariable("boardCode") Integer boardCode) {
        List<LowBoardEntity> lowBoardList = postService.getLowBoard(boardCode);
        return ResponseEntity.ok(lowBoardList);
    }

    @GetMapping("/getAllLowBoard")
    public ResponseEntity<List<LowBoardEntity>> getLowBoardPin() {
        List<LowBoardEntity> lowBoardList = postService.getAllLowBoard();
        System.out.println(lowBoardList);
        return ResponseEntity.ok(lowBoardList);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestParam(value = "attachFile", required = false) List<MultipartFile> attachFile,
                                        @RequestParam("postName") String postName,
                                        @RequestParam("postCon") String postCon,
                                        @RequestParam("lowBoardCode") int lowBoardCode,
                                        @RequestParam(value = "postCommSet", defaultValue = "4") int postCommSet,
                                        @RequestParam(value = "fixStatus", defaultValue = "N") char fixStatus,
                                        @RequestParam(value = "noticeStatus", defaultValue = "N") char noticeStatus,
                                        @RequestParam("psCode") int psCode,
                                        @RequestParam("empCode") int empCode,
                                        Model model) {
        // 상품 정보 저장
        System.out.println("게시글 등록 메소드 작동시작");
        System.out.println(lowBoardCode);
        System.out.println(attachFile);
        System.out.println(postName);
        System.out.println(postCon);
        System.out.println(postCommSet);
        System.out.println(fixStatus);
        System.out.println(noticeStatus);
        System.out.println(psCode);
        System.out.println(empCode);
        PostCommSet commSet = PostCommSet.fromValue(postCommSet);
        System.out.println(commSet);

        /* 상품 등록하기 */
        PostRequest newPost = new PostRequest();
        String lastPostCode = postService.LastPost() != null ? postService.LastPost().getPostCode() : null;
        String nextPostCode;

        if (lastPostCode == null) {
            // 첫 번째 게시글인 경우
            nextPostCode = "PO01";
        } else {
            // 기존 게시글이 있는 경우
            String numericPart = lastPostCode.substring(2); // "020"
            int lastNumber = Integer.parseInt(numericPart); // 20
            int nextNumber = lastNumber + 1;

            // post_code 중복 확인

            nextPostCode = String.format("%02d", nextNumber);
        }

        newPost.setPostCode("PO" + nextPostCode);
        newPost.setPostName(postName);
        newPost.setPostCon(postCon);
        newPost.setPostCommSet(commSet);
        newPost.setFixStatus(fixStatus);
        newPost.setNoticeStatus(noticeStatus);
        newPost.setLowBoardCode(lowBoardCode);
        newPost.setPsCode(psCode);
        newPost.setEmpCode(empCode);
        System.out.println(lowBoardCode);

        System.out.println("newPost" + newPost);
        PostEntity post = postService.insertPost(newPost);
        PostRequest postDTO = new PostRequest();
        postDTO.setPostCode(post.getPostCode());
        postDTO.setPostName(post.getPostName());
        postDTO.setPostCon(post.getPostCon());
        postDTO.setPostCommSet(post.getPostCommSet());
        postDTO.setFixStatus(post.getFixStatus());
        postDTO.setNoticeStatus(post.getNoticeStatus());
        postDTO.setLowBoardCode(post.getLowBoardCode().getLowBoardCode());
        postDTO.setPsCode(post.getPsCode().getPsCode());
        postDTO.setEmpCode(post.getEmpCode().getEmp_code());
        postDTO.setPostDate(post.getPostDate());


        /* 경로 설정 */
        String fileUploadDir = POST_FILE_DIR;

        File dir1 = new File(fileUploadDir);

        /* 디렉토리가 없을 경우 생성한다. */
        if (!dir1.exists()) {
            dir1.mkdirs();
        }
        if (attachFile != null) {
            // 처리 로직

            List<AttachmentEntity> FileList = new ArrayList<>();

            try {
                for (int i = 0; i < attachFile.size(); i++) {
                    /* 첨부파일이 실제로 존재하는 경우 로직 수행 */
                    if (attachFile.get(i).getSize() > 0) {

                        String originalFileName = attachFile.get(i).getOriginalFilename();

                        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                        String saveFileName = UUID.randomUUID() + ext;

                        /* 서버의 설정 디렉토리에 파일 저장하기 */
                        attachFile.get(i).transferTo(new File(fileUploadDir + "/" + saveFileName));

                        /* 가장 최신 게시글 코드 조회 */
                        PostEntity lastPost = postService.LastPost();

                        /* DB에 저장할 파일의 정보 처리 */
                        AttachmentEntity fileInfo = new AttachmentEntity(originalFileName, saveFileName, fileUploadDir + "/" + saveFileName, lastPost.getPostCode());

                        /* 리스트에 파일 정보 저장 */
                        FileList.add(fileInfo);
                    }
                    /* 가장 최신 게시글 코드 조회 */
                    PostEntity lastPost = postService.LastPost();


                }
                /* 파일 리스트를 한 번에 DB에 저장 */
                postService.registFileList(FileList);

                model.addAttribute("message", "파일 업로드에 성공하였습니다.");

            } catch (IOException e) {
                /* 파일 저장 중간에 실패 시, 이전에 저장된 파일 삭제 */

                model.addAttribute("message", "파일 업로드에 실패하였습니다.");
            }

        } else {

            /* 업로드 파일에 대한 정보를 담을 리스트 */

        }
        return ResponseEntity.ok(postDTO);


    }
    @PostMapping("/addReady")
    public ResponseEntity<?> addReadyProduct(    @RequestParam(value = "attachFile", required = false) List<MultipartFile> attachFile,
                                                 @RequestParam(value = "postName", required = false) String postName,
                                                 @RequestParam(value = "postCon", required = false) String postCon,
                                                 @RequestParam(value = "lowBoardCode", required = false) Integer lowBoardCode,
                                                 @RequestParam(value = "postCommSet", defaultValue = "4") Integer postCommSet,
                                                 @RequestParam(value = "fixStatus", defaultValue = "R") Character fixStatus,
                                                 @RequestParam(value = "noticeStatus", defaultValue = "N") Character noticeStatus,
                                                 @RequestParam(value = "psCode", defaultValue = "1") Integer psCode,
                                                 @RequestParam(value = "empCode", required = false) Integer empCode
            ,
                                        Model model) {
        // 상품 정보 저장
        System.out.println("게시글 등록 메소드 작동시작");
        System.out.println(lowBoardCode);
        System.out.println(attachFile);
        System.out.println(postName);
        System.out.println(postCon);
        System.out.println(postCommSet);
        System.out.println(fixStatus);
        System.out.println(noticeStatus);
        System.out.println(psCode);
        System.out.println(empCode);
        PostCommSet commSet = PostCommSet.fromValue(postCommSet);
        System.out.println(commSet);

        /* 상품 등록하기 */
        PostRequest newPost = new PostRequest();
        String numericPart = postService.LastPost().getPostCode().substring(2); // "020"
        int lastNumber = Integer.parseInt(numericPart); // 20
        int nextNumber = lastNumber + 1;

        newPost.setPostCode("PO" + nextNumber);
        newPost.setPostName(postName);
        newPost.setPostCon(postCon);
        newPost.setPostCommSet(commSet);
        newPost.setFixStatus(fixStatus);
        newPost.setNoticeStatus(noticeStatus);
        newPost.setLowBoardCode(7);
        newPost.setPsCode(psCode);
        newPost.setEmpCode(empCode);
        System.out.println(lowBoardCode);

        System.out.println("newPost" + newPost);
        PostEntity post = postService.insertPost(newPost);

        PostRequest postDTO = new PostRequest();
        postDTO.setPostCode(post.getPostCode());
        postDTO.setPostName(post.getPostName());
        postDTO.setPostCon(post.getPostCon());
        postDTO.setPostCommSet(post.getPostCommSet());
        postDTO.setFixStatus(post.getFixStatus());
        postDTO.setNoticeStatus(post.getNoticeStatus());
        postDTO.setLowBoardCode(post.getLowBoardCode().getLowBoardCode());
        postDTO.setPsCode(post.getPsCode().getPsCode());
        postDTO.setEmpCode(post.getEmpCode().getEmp_code());
        postDTO.setPostDate(post.getPostDate());

        /* 경로 설정 */
        String fileUploadDir = POST_FILE_DIR;

        File dir1 = new File(fileUploadDir);

        /* 디렉토리가 없을 경우 생성한다. */
        if (!dir1.exists()) {
            dir1.mkdirs();
        }
        if (attachFile != null) {
            // 처리 로직

            List<AttachmentEntity> FileList = new ArrayList<>();

            try {
                for (int i = 0; i < attachFile.size(); i++) {
                    /* 첨부파일이 실제로 존재하는 경우 로직 수행 */
                    if (attachFile.get(i).getSize() > 0) {

                        String originalFileName = attachFile.get(i).getOriginalFilename();

                        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                        String saveFileName = UUID.randomUUID() + ext;

                        /* 서버의 설정 디렉토리에 파일 저장하기 */
                        attachFile.get(i).transferTo(new File(fileUploadDir + "/" + saveFileName));

                        /* 가장 최신 게시글 코드 조회 */
                        PostEntity lastPost = postService.LastPost();

                        /* DB에 저장할 파일의 정보 처리 */
                        AttachmentEntity fileInfo = new AttachmentEntity(originalFileName, saveFileName, fileUploadDir + "/" + saveFileName, lastPost.getPostCode());

                        /* 리스트에 파일 정보 저장 */
                        FileList.add(fileInfo);
                    }
                    /* 가장 최신 게시글 코드 조회 */
                    PostEntity lastPost = postService.LastPost();


                }
                /* 파일 리스트를 한 번에 DB에 저장 */
                postService.registFileList(FileList);

                model.addAttribute("message", "파일 업로드에 성공하였습니다.");

            } catch (IOException e) {
                /* 파일 저장 중간에 실패 시, 이전에 저장된 파일 삭제 */

                model.addAttribute("message", "파일 업로드에 실패하였습니다.");
            }

        } else {

            /* 업로드 파일에 대한 정보를 담을 리스트 */

        }
        return ResponseEntity.ok(postDTO);


    }

}

