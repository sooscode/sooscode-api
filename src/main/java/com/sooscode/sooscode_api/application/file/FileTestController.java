package com.sooscode.sooscode_api.application.file;

import com.sooscode.sooscode_api.infra.file.S3FileService;
import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class FileTestController {
    private final S3FileService s3FileService;

    //S3 업로드 테스트용
    @PostMapping("/upload")
    public ResponseEntity<?> uploadTest(@RequestParam("file") MultipartFile file) throws Exception {
        SooFile savedFile = s3FileService.uploadImage(file);
        return ResponseEntity.ok(savedFile);
    }
}
