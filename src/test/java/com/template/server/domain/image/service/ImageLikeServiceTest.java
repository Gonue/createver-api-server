package com.template.server.domain.image.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageLike;
import com.template.server.domain.image.repository.gallery.GalleryRepository;
import com.template.server.domain.image.repository.ImageLikeRepository;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class ImageLikeServiceTest {

    @InjectMocks
    private ImageLikeService imageLikeService;

    @Mock
    private ImageLikeRepository imageLikeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GalleryRepository galleryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddLike() {
        // Given
        Long galleryId = 1L;
        String email = "test@test.com";
        Member member = Member.builder()
                .nickName("nickname")
                .password("password")
                .build();
        Gallery gallery = Gallery.builder()
                .prompt("prompt")
                .storageUrl("url")
                .option(1)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(imageLikeRepository.existsByMemberAndGallery(member, gallery)).thenReturn(false);

        // When
        imageLikeService.addLike(galleryId, email);

        // Then
        verify(imageLikeRepository, times(1)).save(any(ImageLike.class));
    }

    @Test
    public void testHasUserLiked() {
        // Given
        Long galleryId = 1L;
        String email = "test@test.com";
        Member member = Member.builder()
                .nickName("nickname")
                .password("password")
                .build();
        Gallery gallery = Gallery.builder()
                .prompt("prompt")
                .storageUrl("url")
                .option(1)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(imageLikeRepository.existsByMemberAndGallery(member, gallery)).thenReturn(true);

        // When
        boolean result = imageLikeService.hasUserLiked(galleryId, email);

        // Then
        assertTrue(result);
    }
}
