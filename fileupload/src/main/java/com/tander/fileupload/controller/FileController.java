// package com.tander.fileupload.controller;

// import com.tander.fileupload.service.FileStorageService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.net.MalformedURLException;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// @RestController
// @RequestMapping("/api/files")
// public class FileController {

// @Value("${file.upload-dir:uploads}")
// private String uploadDir;

// @GetMapping("/{filename:.+}")
// public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
// try {
// Path filePath =
// Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
// Resource resource = new UrlResource(filePath.toUri());

// if (resource.exists()) {
// String contentType = determineContentType(filename);

// return ResponseEntity.ok()
// .contentType(MediaType.parseMediaType(contentType))
// .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +
// resource.getFilename() + "\"")
// .body(resource);
// } else {
// return ResponseEntity.notFound().build();
// }
// } catch (MalformedURLException e) {
// return ResponseEntity.badRequest().build();
// }
// }

// private String determineContentType(String filename) {
// if (filename.endsWith(".pdf")) {
// return "application/pdf";
// } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
// return "image/jpeg";
// } else if (filename.endsWith(".png")) {
// return "image/png";
// } else if (filename.endsWith(".txt")) {
// return "text/plain";
// } else if (filename.endsWith(".doc") || filename.endsWith(".docx")) {
// return "application/msword";
// } else if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
// return "application/vnd.ms-excel";
// } else {
// return "application/octet-stream";
// }
// }
// }