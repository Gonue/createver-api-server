package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.ImageCommentDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageComment;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.image.repository.comment.ImageCommentRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageCommentServiceTest {

    @InjectMocks
    private ImageCommentService imageCommentService;

    @Mock
    private ImageCommentRepository imageCommentRepository;
    @Mock
    private GalleryRepository galleryRepository;
    @Mock
    private MemberRepository memberRepository;

    private Gallery gallery;
    private Member member;
    private ImageComment imageComment;
    private ImageCommentDto imageCommentDto;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .nickName("TestUser")
                .password("password")
                .profileImage("url")
                .roles(List.of("ROLE_USER"))
                .build();

        gallery = Gallery.builder()
                .prompt("Test Gallery Prompt")
                .storageUrl("url")
                .option(1)
                .tags(new ArrayList<>())
                .member(member)
                .build();

        imageComment = ImageComment.builder()
                .content("Test Content")
                .gallery(gallery)
                .member(member)
                .build();
    }

    @Test
    void createCommentTest() {
        // Given
        Long galleryId = 1L;
        String email = "test@example.com";
        String content = "Test Comment";

        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When
        imageCommentService.createComment(galleryId, content, email);

        // Then
        verify(galleryRepository).findById(galleryId);
        verify(memberRepository).findByEmail(email);
        verify(imageCommentRepository).save(any(ImageComment.class));
    }

    @Test
    void updateCommentTest() {
        // Given
        Long imageCommentId = 1L;
        String email = "test@example.com";
        String updatedContent = "Updated Comment";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(imageCommentRepository.findById(imageCommentId)).thenReturn(Optional.of(imageComment));
        when(imageCommentRepository.save(any(ImageComment.class))).thenAnswer(invocation -> {
            ImageComment savedComment = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedComment, "commentId", imageCommentId);
            return savedComment;
        });

        // When
        ImageCommentDto updatedComment = imageCommentService.updateComment(email, imageCommentId, updatedContent);

        // Then
        assertNotNull(updatedComment);
        assertEquals(updatedContent, updatedComment.getContent());
        verify(memberRepository).findByEmail(email);
        verify(imageCommentRepository).findById(imageCommentId);
        verify(imageCommentRepository).save(any(ImageComment.class));
    }

    @Test
    void deleteCommentTest() {
        // Given
        Long imageCommentId = 1L;
        String email = "test@example.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(imageCommentRepository.findById(imageCommentId)).thenReturn(Optional.of(imageComment));

        // When
        imageCommentService.deleteComment(email, imageCommentId);

        // Then
        verify(memberRepository).findByEmail(email);
        verify(imageCommentRepository).findById(imageCommentId);
        verify(imageCommentRepository).delete(any(ImageComment.class));
    }

    @Test
    void getAllCommentsByGalleryIdTest() {
        // Given
        Long galleryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImageComment> page = new PageImpl<>(Arrays.asList(imageComment));
        when(imageCommentRepository.findByGalleryId(galleryId, pageable)).thenReturn(page);

        // When
        Page<ImageCommentDto> result = imageCommentService.getAllCommentsByGalleryId(galleryId, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(imageCommentRepository).findByGalleryId(galleryId, pageable);
    }
}