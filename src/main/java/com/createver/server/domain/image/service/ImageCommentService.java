package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.ImageCommentDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageComment;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.image.repository.comment.ImageCommentRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageCommentService {

    private final ImageCommentRepository imageCommentRepository;
    private final GalleryRepository galleryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createComment(Long galleryId, String content, String email) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        ImageComment comment = ImageComment.builder()
                .content(content)
                .gallery(gallery)
                .member(member)
                .build();
        imageCommentRepository.save(comment);
    }

    @Transactional
    public ImageCommentDto updateComment(String email, Long imageCommentId, String content){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        ImageComment imageComment = imageCommentRepository.findById(imageCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.IMAGE_COMMENT_NOT_FOUND));

        if (!imageComment.getMember().equals(member)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }

        imageComment.updateContent(content);
        return ImageCommentDto.from(imageCommentRepository.save(imageComment));
    }

    @Transactional
    public void deleteComment(String email, Long imageCommentId){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        ImageComment imageComment = imageCommentRepository.findById(imageCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.IMAGE_COMMENT_NOT_FOUND));

        if (!imageComment.getMember().equals(member)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }

        imageCommentRepository.delete(imageComment);
    }

    @Transactional(readOnly = true)
    public Page<ImageCommentDto> getAllCommentsByGalleryId(Long galleryId, Pageable pageable) {
        return imageCommentRepository.findByGalleryId(galleryId, pageable)
                .map(ImageCommentDto::from);
    }
}
