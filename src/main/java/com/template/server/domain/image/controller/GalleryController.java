package com.template.server.domain.image.controller;

import com.template.server.domain.image.dto.response.GalleryResponse;
import com.template.server.domain.image.service.GalleryService;
import com.template.server.domain.image.service.S3DownloadService;
import com.template.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class GalleryController {

    private final GalleryService galleryService;
    private final S3DownloadService s3DownloadService;


    @GetMapping("/list")
    public Response<Page<GalleryResponse>> galleryList(Pageable pageable){
        return Response.success(galleryService.galleryList(pageable).map(GalleryResponse::from));
    }

    @GetMapping("/list/search")
    public Response<Page<GalleryResponse>> findGalleryList(@RequestParam String prompt, Pageable pageable){
        return Response.success(galleryService.findGalleryList(prompt, pageable).map(GalleryResponse::from));
    }

    @GetMapping("/download/{galleryId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long galleryId) {
        byte[] imageData = s3DownloadService.downloadFileByGalleryId(galleryId);

        if (imageData == null) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageData.length);

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String fileName = generateRandomKey(8) + "_" + currentDate;
        headers.setContentDispositionFormData("attachment", fileName + ".png");
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    private String generateRandomKey(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder keyBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            keyBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        return keyBuilder.toString();
    }
}
