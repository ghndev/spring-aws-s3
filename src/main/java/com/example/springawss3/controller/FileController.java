package com.example.springawss3.controller;

import com.example.springawss3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        if (s3Service.isValid(file)) {
            s3Service.uploadFile(file);
            redirectAttributes.addAttribute("message", "success");
        } else {
            redirectAttributes.addAttribute("message", "failed");
        }

        return "redirect:/";
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String filename) {
        byte[] data = s3Service.getObject(filename);
        String extension = filename.substring(filename.length() - 4);
        filename = filename.substring(0, filename.length() - 40);

        ByteArrayResource resource = new ByteArrayResource(data);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(data.length);
        httpHeaders.setContentDispositionFormData("attachment", filename + extension);

        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }

}
