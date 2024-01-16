package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.response.GalleryResponse;
import com.createver.server.domain.image.service.GalleryService;
import com.createver.server.global.error.response.Response;
import com.createver.server.global.util.aws.service.S3DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/list/gallery")
    public Response<Page<GalleryResponse>> galleryListWithCommentCountAndLikeCount(Pageable pageable){
        return Response.success(galleryService.galleryListWithComment(pageable).map(GalleryResponse::from));
    }

    @GetMapping("/admin/list/gallery")
    public Response<Page<GalleryResponse>> adminGalleryList(Pageable pageable) {
        return Response.success(galleryService.adminGalleryList(pageable).map(GalleryResponse::from));
    }

    @GetMapping("/list/search")
    public Response<Page<GalleryResponse>> findGalleryList(
            @RequestParam String prompt,
            @RequestParam(required = false)
            List<Integer> options,
            Pageable pageable) {
        return Response.success(galleryService.findGalleryListByOptionsAndPrompt(options, prompt, pageable).map(GalleryResponse::from));
    }

    @GetMapping("/list/tag")
    public Response<Page<GalleryResponse>> findGalleryListByTag(@RequestParam String tagName, Pageable pageable){
        return Response.success(galleryService.getGalleriesByTagName(tagName, pageable).map(GalleryResponse::from));
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
