package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.ImageCommentDto;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageComment;
import com.template.server.domain.image.repository.gallery.GalleryRepository;
import com.template.server.domain.image.repository.ImageCommentRepository;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageCommentServiceTest {

    @InjectMocks
    private ImageCommentService imageCommentService;

    @Mock
    private ImageCommentRepository imageCommentRepository;

    @Mock
    private GalleryRepository galleryRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment() {
        // Given
        Long galleryId = 1L;
        String content = "Nice image!";
        String email = "test@test.com";

        Gallery gallery = mock(Gallery.class);
        Member member = mock(Member.class);

        when(galleryRepository.findById(galleryId)).thenReturn(Optional.of(gallery));
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When
        imageCommentService.createComment(galleryId, content, email);

        // Then
        verify(imageCommentRepository, times(1)).save(any(ImageComment.class));
    }

    @Test
    void testUpdateComment() {
        // Given
        String email = "test@test.com";
        Long imageCommentId = 1L;
        String content = "Updated comment";

        Member member = mock(Member.class);
        ImageComment imageComment = mock(ImageComment.class);
        Gallery gallery = mock(Gallery.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(imageCommentRepository.findById(imageCommentId)).thenReturn(Optional.of(imageComment));
        when(imageComment.getMember()).thenReturn(member);
        when(imageComment.getGallery()).thenReturn(gallery);
        when(gallery.getGalleryId()).thenReturn(1L);

        when(imageComment.getCommentId()).thenReturn(1L);
        when(imageCommentRepository.save(any(ImageComment.class))).thenReturn(imageComment);

        // When
        imageCommentService.updateComment(email, imageCommentId, content);

        // Then
        verify(imageComment, times(1)).updateContent(content);
        verify(imageCommentRepository, times(1)).save(any(ImageComment.class));
    }


    @Test
    void testDeleteComment() {
        // Given
        Long commentId = 1L;
        String email = "test@test.com";

        Member member = mock(Member.class);
        ImageComment comment = mock(ImageComment.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(imageCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(comment.getMember()).thenReturn(member);

        // When
        imageCommentService.deleteComment(email, commentId);

        // Then
        verify(imageCommentRepository, times(1)).delete(comment);
    }

    @Test
    void testGetAllCommentsByGalleryId() {
        // Given
        Long galleryId = 1L;
        Pageable pageable = mock(Pageable.class);

        Gallery mockGallery = mock(Gallery.class);
        Member mockMember = mock(Member.class);

        ImageComment comment1 = ImageComment.builder()
                                            .content("comment1")
                                            .gallery(mockGallery)
                                            .member(mockMember)
                                            .build();
        ImageComment comment2 = ImageComment.builder()
                                            .content("comment2")
                                            .gallery(mockGallery)
                                            .member(mockMember)
                                            .build();
        Page<ImageComment> commentPage = new PageImpl<>(Arrays.asList(comment1, comment2));

        when(imageCommentRepository.findByGallery_GalleryId(galleryId, pageable)).thenReturn(commentPage);

        // When
        Page<ImageCommentDto> result = imageCommentService.getAllCommentsByGalleryId(galleryId, pageable);

        // Then
        assertEquals(2, result.getContent().size());
    }
}