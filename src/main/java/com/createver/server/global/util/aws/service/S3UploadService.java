package com.createver.server.global.util.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.createver.server.global.util.aws.CloudFrontUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
    private final RestTemplate restTemplate;

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

    public String uploadWav(byte[] wavData) {
        String s3FileName = "musics/" + generateShortUuid() + ".wav";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(wavData.length);
        objectMetadata.setContentDisposition("inline");
        objectMetadata.setContentType("audio/wav");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(wavData);

        amazonS3.putObject(bucket, s3FileName, byteArrayInputStream, objectMetadata);

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    public String uploadWavAndReturnCloudFrontUrl(byte[] wavData) {
        String s3Url = uploadWav(wavData);
        return CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url);
    }

    public String uploadFromUrl(String imageUrl, String contentType) {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to download image from URL: " + imageUrl);
        }

        return upload(response.getBody(), contentType);
    }

}
