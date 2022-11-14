package com.example.springawss3.controller;

import com.example.springawss3.domain.File;
import com.example.springawss3.service.S3Service;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;

    @GetMapping("/editor")
    public String editor() {
        return "editor";
    }

    @PostMapping(value="/uploadSummernoteImageFile", produces = "application/json")
    @ResponseBody
    public JsonObject uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile) {
        Long id = s3Service.uploadFile(multipartFile);
        File file = s3Service.findFileById(id);
        String filename = file.getOriginalFilename();
        // 실제 프로젝트에선 게시판이 삭제될때 게시판과 매핑

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", s3Service.getBucketUrl() + "/" + filename);
        jsonObject.addProperty("responseCode", "success");

        return jsonObject;
    }

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

    @GetMapping("/delete/{filename}")
    public String delete(@PathVariable String filename) {
        s3Service.deleteFile(filename);

        return "redirect:/";
    }

    @GetMapping("/preview/{filename}")
    public String preview(@PathVariable String filename) {
        return "preview";
    }

}
