package com.createver.server.global.util.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3DownloadServiceTest {

    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private GalleryRepository galleryRepository;
    @InjectMocks
    private S3DownloadService s3DownloadService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3DownloadService, "bucket", "test-bucket");
    }

    @Test
    @DisplayName("파일 다운로드 - Gallery가 존재하는 경우")
    void downloadFileByGalleryIdExists() throws Exception {
        // given
        Long galleryId = 1L;
        Gallery gallery = Gallery.builder()
                        .prompt("test prompt")
                        .storageUrl("url")
                        .option(1)
                        .tags(new ArrayList<>())
                        .member(null)
                        .build();
        when(galleryRepository.findGalleryByIdForUpdate(galleryId)).thenReturn(Optional.of(gallery));

        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream(new byte[10]));
        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

        // when
        byte[] downloadedData = s3DownloadService.downloadFileByGalleryId(galleryId);

        // then
        assertNotNull(downloadedData);
        verify(galleryRepository).findGalleryByIdForUpdate(galleryId);
        verify(amazonS3).getObject(any(GetObjectRequest.class));
        verify(galleryRepository).save(any(Gallery.class));
    }

    @Test
    @DisplayName("파일 다운로드 - Gallery가 존재하지 않는 경우")
    void downloadFileByGalleryIdNotExists() {
        // given
        Long galleryId = 999L;
        when(galleryRepository.findGalleryByIdForUpdate(galleryId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessLogicException.class, () -> s3DownloadService.downloadFileByGalleryId(galleryId));
    }

    @Test
    @DisplayName("S3에서 예외 발생 시 예외 처리")
    void downloadFileFromS3WithException() {
        // given
        Long galleryId = 1L;
        Gallery gallery = Gallery.builder()
                        .storageUrl("http://valid-url.com/file")
                        .build();
        when(galleryRepository.findGalleryByIdForUpdate(galleryId)).thenReturn(Optional.of(gallery));
        when(amazonS3.getObject(any(GetObjectRequest.class))).thenThrow(RuntimeException.class);

        // when
        byte[] result = s3DownloadService.downloadFileByGalleryId(galleryId);

        // then
        assertNull(result);
    }
}