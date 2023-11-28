package com.template.server.global.util.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.template.server.global.util.aws.CloudFrontUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    private String generateShortUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 16);
    }
    public String upload(MultipartFile multipartFile) throws IOException {
        String s3FileName = "images/" + generateShortUuid() + ".png";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getInputStream().available());
        objectMetadata.setContentDisposition("inline"); // 이미지를 인라인으로 표시하도록 지시
        objectMetadata.setContentType(multipartFile.getContentType()); // 이미지의 콘텐츠 타입 설정

        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objectMetadata);

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    public String upload(byte[] imageData, String contentType) {
        String s3FileName = "images/" + generateShortUuid() + ".png";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageData.length);
        objectMetadata.setContentDisposition("inline");
        objectMetadata.setContentType(contentType);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);

        amazonS3.putObject(bucket, s3FileName, byteArrayInputStream, objectMetadata);

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    public String uploadAndReturnCloudFrontUrl(MultipartFile multipartFile) throws IOException {
        String s3FileName = "images/" + generateShortUuid() + ".png";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getInputStream().available());
        objectMetadata.setContentDisposition("inline");
        objectMetadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objectMetadata);

        String s3Url = amazonS3.getUrl(bucket, s3FileName).toString();
        return CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url);
    }

    public String uploadAndReturnCloudFrontUrl(byte[] imageData, String contentType) {
        String s3FileName = "images/" + generateShortUuid() + ".png";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageData.length);
        objectMetadata.setContentDisposition("inline");
        objectMetadata.setContentType(contentType);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);

        amazonS3.putObject(bucket, s3FileName, byteArrayInputStream, objectMetadata);

        String s3Url = amazonS3.getUrl(bucket, s3FileName).toString();
        return CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url);
    }

    public String uploadMp3(byte[] mp3Data) {
        String s3FileName = "musics/" + generateShortUuid() + ".mp3";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(mp3Data.length);
        objectMetadata.setContentDisposition("inline");
        objectMetadata.setContentType("audio/mpeg");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mp3Data);

        amazonS3.putObject(bucket, s3FileName, byteArrayInputStream, objectMetadata);

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    public String uploadMp3AndReturnCloudFrontUrl(byte[] mp3Data) {
        String s3Url = uploadMp3(mp3Data);
        return CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url);
    }
}
