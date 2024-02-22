package com.createver.server.domain.image.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageLike;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.image.repository.like.ImageLikeRepository;
import com.createver.server.domain.image.service.like.ImageLikeService;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@DisplayName("Image Like Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageLikeServiceTest {

    @InjectMocks
    private ImageLikeService imageLikeService;

    @Mock
    private ImageLikeRepository imageLikeRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GalleryRepository galleryRepository;

    private Gallery gallery;
    private Member member;
    private ImageLike imageLike;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                       .email("test@example.com")
                       .nickName("TestUser")
                       .password("password")
                       .profileImage("url")
                       .build();

        gallery = Gallery.builder()
                         .prompt("Test Gallery Prompt")
                         .storageUrl("url")
                         .option(1)
                         .build();

        imageLike = ImageLike.builder()
                             .member(member)
                             .gallery(gallery)
                             .build();
    }

    @DisplayName("갤러리에 좋아요 추가")
    @Test
    void addLikeTest() {
        // Given
        Long galleryId = 1L;
        String email = "test@example.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(imageLikeRepository.existsByMemberAndGallery(member, gallery)).thenReturn(false);

        // When
        imageLikeService.addLike(galleryId, email);

        // Then
        verify(memberRepository).findByEmail(email);
        verify(galleryRepository).findById(galleryId);
        verify(imageLikeRepository).existsByMemberAndGallery(member, gallery);
        verify(imageLikeRepository).save(any(ImageLike.class));
    }

    @DisplayName("사용자가 이미 좋아요를 누른 경우")
    @Test
    void hasUserLikedTest() {
        // Given
        Long galleryId = 1L;
        String email = "test@example.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(imageLikeRepository.existsByMemberAndGallery(member, gallery)).thenReturn(true);

        // When
        boolean hasLiked = imageLikeService.hasUserLiked(galleryId, email);

        // Then
        assertTrue(hasLiked);
        verify(memberRepository).findByEmail(email);
        verify(galleryRepository).findById(galleryId);
        verify(imageLikeRepository).existsByMemberAndGallery(member, gallery);
    }

    @DisplayName("사용자가 특정 갤러리에 좋아요를 눌렀는지 확인")
    @Test
    void countLikesForGalleryTest() {
        // Given
        Long galleryId = 1L;
        String email = "test@example.com";
        Gallery mockGallery = Mockito.spy(Gallery.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(mockGallery));

        // When
        imageLikeService.addLike(galleryId, email);

        // Then
        verify(mockGallery).increaseLikeCount(); // increaseLikeCount 호출 검증
        verify(galleryRepository).findById(galleryId);
    }

    @DisplayName("특정 갤러리의 총 좋아요 수 확인")
    @Test
    void countTotalLikesForGalleryTest() {
        // Given
        Long galleryId = 1L;
        String email = "test@example.com";

        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(imageLikeRepository.existsByMemberAndGallery(member, gallery)).thenReturn(false);

        // When
        imageLikeService.addLike(galleryId, email);
        Long count = imageLikeService.countLikesForGallery(galleryId);

        // Then
        assertEquals(1L, count);
        verify(galleryRepository, times(2)).findById(galleryId); // findById가 두 번 호출됨
        verify(imageLikeRepository).existsByMemberAndGallery(member, gallery);
        verify(imageLikeRepository).save(any(ImageLike.class));
    }
}
