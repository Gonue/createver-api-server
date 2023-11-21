package com.template.server.domain.image.service;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageView;
import com.template.server.domain.image.repository.gallery.GalleryRepository;
import com.template.server.domain.image.repository.view.ImageViewRepository;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageViewService {

    private final ImageViewRepository imageViewRepository;
    private final MemberRepository memberRepository;
    private final GalleryRepository galleryRepository;


    @Transactional
    public void addView(Long galleryId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));

        ImageView imageView = ImageView.builder()
                .member(member)
                .gallery(gallery)
                .build();
        imageViewRepository.save(imageView);
    }

    public Long countViewsForGallery(Long galleryId) {
        return imageViewRepository.countByGalleryId(galleryId);
    }

    public List<ImageView> getViewHistory(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return imageViewRepository.findByMemberOrderByViewedAtDesc(member);
    }
}
