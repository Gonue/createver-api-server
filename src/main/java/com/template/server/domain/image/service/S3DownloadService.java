package com.template.server.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.repository.GalleryRepository;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class S3DownloadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final GalleryRepository galleryRepository;

    public byte[] downloadFileByGalleryId(Long galleryId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.S3_FILE_ERROR, "해당 galleryId가 존재하지 않습니다."));

        String key = extractKeyFromStorageUrl(gallery.getStorageUrl());

        return downloadFileFromS3(key);
    }

    private String extractKeyFromStorageUrl(String storageUrl) {
        try {
            URI uri = new URI(storageUrl);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (URISyntaxException e) {
            throw new BusinessLogicException(ExceptionCode.S3_FILE_ERROR, "잘못된 URL 형식입니다.");
        }
    }

    private byte[] downloadFileFromS3(String key) {
        try {
            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, key));
            try (InputStream objectInputStream = s3Object.getObjectContent()) {
                return IOUtils.toByteArray(objectInputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
