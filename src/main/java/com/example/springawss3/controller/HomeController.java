package com.example.springawss3.controller;

import com.example.springawss3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final S3Service s3Service;

    @GetMapping
    public String home(@RequestParam(value = "message", required = false) String message, Model model) {

        List<String> filenames = s3Service.findAll();

        model.addAttribute("message", message);
        model.addAttribute("files", filenames);

        return "home";
    }
}
