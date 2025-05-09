package com.tander.fileupload.service;

import com.tander.fileupload.dto.FileUploadDto;
import com.tander.fileupload.exception.FileNotFoundException;
import com.tander.fileupload.exception.FileStorageException;
import com.tander.fileupload.model.FileUpload;
import com.tander.fileupload.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public FileUploadDto uploadFile(MultipartFile file) {
        try {

            // Store the file physically
            String filePath = fileStorageService.storeFile(file);
            String filename = fileStorageService.generateFileName(filePath);

            // Generate URL for accessing the file
            String fileUrl = fileStorageService.generateFileUrl(filename);

            // Create database record
            FileUpload FileUpload = com.tander.fileupload.model.FileUpload.builder()
                    .filename(filename)
                    .path(filePath)
                    .fileUrl(fileUrl)
                    .contentType(file.getContentType())
                    .build();

            FileUpload = fileRepository.save(FileUpload);

            return convertToDTO(FileUpload);
        } catch (IOException ex) {
            throw new FileStorageException(
                    "Could not store file " + file.getOriginalFilename() + ":" + ex.getMessage());
        }
    }

    @Transactional
    public void deleteFile(Long fileId) {
        FileUpload FileUpload = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with id: " + fileId));

        try {
            fileStorageService.deleteFile(FileUpload.getPath());
            fileRepository.delete(FileUpload);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + FileUpload.getFilename() + ":" + ex.getMessage());
        }
    }

    public FileUploadDto getFile(Long fileId) {
        FileUpload FileUpload = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with id: " + fileId));

        return convertToDTO(FileUpload);
    }

    public FileUploadDto getFileByFilename(String filename) {
        FileUpload FileUpload = fileRepository.findByFilename(filename)
                .orElseThrow(() -> new FileNotFoundException("File not found with filename: " + filename));

        return convertToDTO(FileUpload);
    }

    private FileUploadDto convertToDTO(FileUpload FileUpload) {
        return FileUploadDto.builder()
                .id(FileUpload.getId())
                .filename(FileUpload.getFilename())
                .fileUrl(FileUpload.getFileUrl())
                .contentType(FileUpload.getContentType())
                .build();
    }
}