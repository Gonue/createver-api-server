package com.createver.server.global.util.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ImageAvatarRepository imageAvatarRepository;

    @Transactional
    public byte[] downloadFileByGalleryId(Long galleryId) {
        Gallery gallery = galleryRepository.findGalleryByIdForUpdate(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.S3_FILE_ERROR, "해당 galleryId가 존재하지 않습니다."));

        gallery.increaseDownloadCount();
        galleryRepository.save(gallery);

        String key = extractKeyFromStorageUrl(gallery.getStorageUrl());

        return downloadFileFromS3(key);
    }

    private String extractKeyFromStorageUrl(String storageUrl) {
        try {
            URI uri = new URI(storageUrl);
            String path = uri.getPath();
            return path.substring(1);
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

    public byte[] downloadImageByPredictionId(String predictionId) {
        ImageAvatar imageAvatar = imageAvatarRepository.findByPredictionId(predictionId);

        if (imageAvatar == null) {
            throw new BusinessLogicException(ExceptionCode.S3_FILE_ERROR, "해당 predictionId가 존재하지 않습니다.");
        }

        String key = extractKeyFromStorageUrl(imageAvatar.getResultImageUrl());
        return downloadFileFromS3(key);
    }
}
