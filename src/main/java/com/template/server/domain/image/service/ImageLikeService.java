package com.template.server.domain.image.service;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageLike;
import com.template.server.domain.image.repository.GalleryRepository;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.image.repository.ImageLikeRepository;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

        ImageLike imageLike = ImageLike.create(member, gallery);
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
        return imageLikeRepository.countByGalleryGalleryId(galleryId);
    }
}
