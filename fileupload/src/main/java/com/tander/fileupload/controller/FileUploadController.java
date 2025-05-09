package com.tander.fileupload.controller;

import com.tander.fileupload.dto.FileUploadDto;
import com.tander.fileupload.service.FileUploadService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/files")
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.service.url:http://localhost:8010}")
    private String fileServiceUrl;

    @Autowired
    private FileUploadService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadDto> uploadFile(
            @RequestParam MultipartFile file) {

        FileUploadDto response = fileService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<FileUploadDto> getFileInfo(@PathVariable Long fileId) {
        FileUploadDto fileResponse = fileService.getFile(fileId);
        return ResponseEntity.ok(fileResponse);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "File deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            FileUploadDto fileInfo = fileService.getFileByFilename(filename);
            String fileUrl = fileInfo.getFileUrl().replace(fileServiceUrl, "");
            fileUrl = fileUrl.replace("/api/files/", "");
            fileUrl = uploadDir + "/" + fileUrl;
            Path filePath = Paths.get(fileUrl);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                        .body(resource);
            } else {
                log.error("Found no file " + filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Encountered error while serving file " + filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/debug/{filename:.+}")
    public ResponseEntity<String> debugFileInfo(@PathVariable String filename) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(filename);

            StringBuilder info = new StringBuilder();
            info.append("Upload directory: ").append(uploadPath).append("\n");
            info.append("File path: ").append(filePath).append("\n");
            info.append("File exists: ").append(Files.exists(filePath)).append("\n");

            if (Files.exists(filePath)) {
                info.append("File size: ").append(Files.size(filePath)).append(" bytes\n");
                info.append("File readable: ").append(Files.isReadable(filePath)).append("\n");
            }

            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(info.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}