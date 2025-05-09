package com.tander.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileuploadApplication {

    public static void main(String[] args) {
        DatabaseInitializer.initialize("fileuploadservice_db");
        SpringApplication.run(FileuploadApplication.class, args);
    }

}
