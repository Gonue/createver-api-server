package com.createver.server.global.util.aws.controller;

import com.createver.server.global.util.aws.service.S3UploadService;
import com.createver.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class S3UploadController {

   private final S3UploadService s3UploadService;

   @PostMapping("/upload")
   public Response<String> uploadFile(@RequestParam("images") MultipartFile multipartFile) throws IOException {
       String url = s3UploadService.upload(multipartFile);
       return Response.success(200,url);
   }

   @PostMapping("/upload/cdn")
   public Response<String> uploadAndReturnCloudFrontUrl(@RequestParam("images") MultipartFile multipartFile) throws IOException {
       String url = s3UploadService.uploadAndReturnCloudFrontUrl(multipartFile);
       return Response.success(200,url);
   }
   
}
