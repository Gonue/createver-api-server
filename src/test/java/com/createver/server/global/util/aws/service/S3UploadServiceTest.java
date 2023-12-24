package com.createver.server.global.util.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.createver.server.global.util.aws.CloudFrontUrlUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3UploadServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3UploadService s3UploadService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3UploadService, "bucket", "test-bucket");
    }

    @Test
    @DisplayName("이미지 업로드 테스트")
    void testUploadImage() throws IOException {
        // given
        MultipartFile mockFile = mock(MultipartFile.class);
        String expectedUrl = "http://example.com/s3/bucket/images/test.png";
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[10]));
        when(mockFile.getContentType()).thenReturn("image/png");
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(expectedUrl));

        // when
        String actualUrl = s3UploadService.upload(mockFile);

        // then
        assertEquals(expectedUrl, actualUrl);
        verify(amazonS3).putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("바이트 배열 업로드 테스트")
    void testUploadByteArray() throws MalformedURLException {
        byte[] imageData = new byte[10];
        String expectedUrl = "http://example.com/s3/bucket/images/test.png";
        URL url = new URL(expectedUrl);
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(url);

        String actualUrl = s3UploadService.upload(imageData, "image/png");

        assertEquals(expectedUrl, actualUrl);
        verify(amazonS3).putObject(anyString(), anyString(), any(ByteArrayInputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("MultipartFile을 CloudFront URL로 업로드 테스트")
    void testUploadAndReturnCloudFrontUrlMultipartFile() throws IOException {
        try (MockedStatic<CloudFrontUrlUtils> mockedStatic = Mockito.mockStatic(CloudFrontUrlUtils.class)) {
            // given
            MultipartFile mockFile = mock(MultipartFile.class);
            String s3Url = "http://example.com/s3/bucket/images/test.png";
            String expectedUrl = "http://cloudfront.net/images/test.png";
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[10]));
            when(mockFile.getContentType()).thenReturn("image/png");
            when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(s3Url));
            mockedStatic.when(() -> CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url)).thenReturn(expectedUrl);

            // when
            String actualUrl = s3UploadService.uploadAndReturnCloudFrontUrl(mockFile);

            // then
            assertEquals(expectedUrl, actualUrl);
        }
    }

    @Test
    @DisplayName("바이트 배열을 CloudFront URL로 업로드 테스트")
    void testUploadAndReturnCloudFrontUrlByteArray() throws MalformedURLException {
        try (MockedStatic<CloudFrontUrlUtils> mockedStatic = Mockito.mockStatic(CloudFrontUrlUtils.class)) {
            // given
            byte[] imageData = new byte[10];
            String s3Url = "http://example.com/s3/bucket/images/test.png";
            String expectedUrl = "http://cloudfront.net/images/test.png";
            when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(s3Url));
            mockedStatic.when(() -> CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url)).thenReturn(expectedUrl);

            // when
            String actualUrl = s3UploadService.uploadAndReturnCloudFrontUrl(imageData, "image/png");

            // then
            assertEquals(expectedUrl, actualUrl);
            verify(amazonS3).putObject(anyString(), anyString(), any(ByteArrayInputStream.class), any(ObjectMetadata.class));
        }
    }

    @Test
    @DisplayName("WAV 파일 업로드 테스트")
    void testUploadWav() throws MalformedURLException {
        // given
        byte[] wavData = new byte[10];
        String expectedUrl = "http://example.com/s3/bucket/musics/test.wav";
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(expectedUrl));

        // when
        String actualUrl = s3UploadService.uploadWav(wavData);

        // then
        assertEquals(expectedUrl, actualUrl);
        verify(amazonS3).putObject(anyString(), anyString(), any(ByteArrayInputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("WAV 파일을 CloudFront URL로 업로드 테스트")
    void testUploadWavAndReturnCloudFrontUrl() throws MalformedURLException {
        try (MockedStatic<CloudFrontUrlUtils> mockedStatic = Mockito.mockStatic(CloudFrontUrlUtils.class)) {
            // given
            byte[] wavData = new byte[10];
            String s3Url = "http://example.com/s3/bucket/musics/test.wav";
            String expectedUrl = "http://cloudfront.net/musics/test.wav";
            when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(s3Url));
            mockedStatic.when(() -> CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url)).thenReturn(expectedUrl);

            // when
            String actualUrl = s3UploadService.uploadWavAndReturnCloudFrontUrl(wavData);

            // then
            assertEquals(expectedUrl, actualUrl);
            verify(amazonS3).putObject(anyString(), anyString(), any(ByteArrayInputStream.class), any(ObjectMetadata.class));
        }
    }
}