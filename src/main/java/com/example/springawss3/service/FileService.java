package com.example.springawss3.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    void uploadFile(MultipartFile file);

    byte[] getObject(String filename);

    String deleteFile(String filename);

    List<String> findAll();
}
