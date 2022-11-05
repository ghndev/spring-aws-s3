package com.example.springawss3.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * A DTO for the {@link File} entity
 */
@Getter
public class FileDto {

    private final String originalFilename;
    private final String fullPath;

    @Builder
    public FileDto(String originalFilename, String fullPath) {
        this.originalFilename = originalFilename;
        this.fullPath = fullPath;
    }
}