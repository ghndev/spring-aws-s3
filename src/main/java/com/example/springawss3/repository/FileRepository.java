package com.example.springawss3.repository;

import com.example.springawss3.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    File findFileByOriginalFilename(String filename);
}
