package com.createver.server.domain.image.service;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageLike;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.image.repository.like.ImageLikeRepository;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ImageLikeService {
    private final ImageLikeRepository imageLikeRepository;
    private final MemberRepository memberRepository;
    private final GalleryRepository galleryRepository;

    @Transactional
    public void addLike(Long galleryId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));

        boolean alreadyLiked = imageLikeRepository.existsByMemberAndGallery(member, gallery);
        if (alreadyLiked) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_LIKED);
        }

        ImageLike imageLike = ImageLike.builder()
                .member(member)
                .gallery(gallery)
                .build();
        gallery.increaseLikeCount();
        imageLikeRepository.save(imageLike);
    }

    public boolean hasUserLiked(Long galleryId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));

        return imageLikeRepository.existsByMemberAndGallery(member, gallery);
    }

    public Long countLikesForGallery(Long galleryId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));
        return (long) gallery.getLikeCount();
    }
}
