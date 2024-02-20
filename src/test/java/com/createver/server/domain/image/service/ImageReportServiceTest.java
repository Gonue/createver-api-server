package com.createver.server.domain.image.service;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageReport;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.image.repository.report.ImageReportRepository;
import com.createver.server.domain.image.service.report.ImageReportService;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Image Report Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageReportServiceTest {

    @InjectMocks
    private ImageReportService imageReportService;

    @Mock
    private ImageReportRepository imageReportRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GalleryRepository galleryRepository;

    private Member member;
    private Gallery gallery;

    @BeforeEach
    void setUp() {
        member = Member.builder().email("test@example.com").build();
        gallery = Gallery.builder().prompt("test").storageUrl("testUrl").build();
    }

    @DisplayName("갤러리에 신고 추가")
    @Test
    void addReportTest() {
        String email = member.getEmail();
        Long galleryId = 1L;
        String content = "신고 내용";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(imageReportRepository.existsByMemberAndGallery(member, gallery)).thenReturn(false);

        imageReportService.addReport(galleryId, email, content);

        verify(memberRepository).findByEmail(email);
        verify(galleryRepository).findById(galleryId);
        verify(imageReportRepository).save(any(ImageReport.class));
        verify(imageReportRepository).existsByMemberAndGallery(member, gallery);
    }

    @DisplayName("사용자가 이미 갤러리를 신고했는지 확인")
    @Test
    void hasUserReportedTest() {
        String email = member.getEmail();
        Long galleryId = gallery.getGalleryId();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(imageReportRepository.existsByMemberAndGallery(member, gallery)).thenReturn(true);

        boolean hasReported = imageReportService.hasUserReported(galleryId, email);

        assertTrue(hasReported);
        verify(memberRepository).findByEmail(email);
        verify(galleryRepository).findById(galleryId);
        verify(imageReportRepository).existsByMemberAndGallery(member, gallery);
    }

    @DisplayName("특정 갤러리의 신고 횟수 확인")
    @Test
    void countReportsForGalleryTest() {
        Long galleryId = gallery.getGalleryId();

        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));

        Long count = imageReportService.countReportsForGallery(galleryId);

        assertEquals(0L, count);
        verify(galleryRepository).findById(galleryId);
    }

    @DisplayName("갤러리 블라인드 상태 업데이트")
    @Test
    void updateGalleryBlindStatusTest() {
        Long galleryId = gallery.getGalleryId();
        boolean isBlinded = true;

        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));

        imageReportService.updateGalleryBlindStatus(galleryId, isBlinded);

        verify(galleryRepository).findById(galleryId);
        verify(galleryRepository).save(gallery);
        assertEquals(isBlinded, gallery.isBlinded());
    }

}