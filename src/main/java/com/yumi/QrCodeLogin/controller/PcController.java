package com.yumi.QrCodeLogin.controller;

import com.yumi.QrCodeLogin.respository.CodeStateRepository;
import com.yumi.QrCodeLogin.util.QRCodeGenerator;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/pc")
@Controller
public class PcController {

    @Resource
    private CodeStateRepository codeStateRepository;


    @GetMapping("/qrLogin")
    public String loginPage() {
        return "rqLogin";
    }

    @GetMapping("/qrCode")
    public ResponseEntity<byte[]> qrCode() {
        String code = UUID.randomUUID().toString();
        codeStateRepository.addCode(code);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(QRCodeGenerator.generate(code));
    }
}
