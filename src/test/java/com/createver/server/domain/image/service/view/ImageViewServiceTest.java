package com.createver.server.domain.image.service.view;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageView;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.image.repository.view.ImageViewRepository;
import com.createver.server.domain.image.service.view.ImageViewService;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Image View Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageViewServiceTest {

    @InjectMocks
    private ImageViewService imageViewService;

    @Mock
    private ImageViewRepository imageViewRepository;
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

    @DisplayName("갤러리 조회 추가")
    @Test
    void addViewTest() {
        Long galleryId = 1L;
        String email = "test@example.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));

        imageViewService.addView(galleryId, email);

        verify(memberRepository).findByEmail(email);
        verify(galleryRepository).findById(galleryId);
        verify(imageViewRepository).save(any(ImageView.class));
    }

    @DisplayName("특정 갤러리의 총 조회 수 확인")
    @Test
    void countViewsForGalleryTest() {
        Long galleryId = 1L;
        Long expectedCount = 5L;

        when(imageViewRepository.countByGalleryId(galleryId)).thenReturn(expectedCount);

        Long count = imageViewService.countViewsForGallery(galleryId);

        assertEquals(expectedCount, count);
        verify(imageViewRepository).countByGalleryId(galleryId);
    }

    @DisplayName("사용자의 갤러리 조회 이력 확인")
    @Test
    void getViewHistoryTest() {
        String email = "test@example.com";
        LocalDateTime now = LocalDateTime.now();

        List<ImageView> viewHistory = List.of(
            ImageView.builder().member(member).gallery(gallery).viewedAt(now).build(),
            ImageView.builder().member(member).gallery(gallery).viewedAt(now.minusDays(1)).build()
        );


        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(imageViewRepository.findByMemberOrderByViewedAtDesc(member)).thenReturn(viewHistory);

        List<ImageView> result = imageViewService.getViewHistory(email);

        assertEquals(viewHistory, result);
        verify(memberRepository).findByEmail(email);
        verify(imageViewRepository).findByMemberOrderByViewedAtDesc(member);
    }

}