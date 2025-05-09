package com.tander.fileupload.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tander.fileupload.model.FileUpload;

@Repository
public interface FileUploadRepository extends CrudRepository<FileUpload, Long> {

    Optional<FileUpload> findByFilename(String filename);
}
