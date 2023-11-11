package com.template.server.domain.image.service;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageReport;
import com.template.server.domain.image.repository.GalleryRepository;
import com.template.server.domain.image.repository.ImageReportRepository;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ImageReportService {
    private final ImageReportRepository imageReportRepository;
    private final MemberRepository memberRepository;
    private final GalleryRepository galleryRepository;

    @Transactional
    public void addReport(Long galleryId, String email, String content) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));

        boolean alreadyReported = imageReportRepository.existsByMemberAndGallery(member, gallery);
        if (alreadyReported) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_REPORTED);
        }

        ImageReport imageReport = ImageReport.create(member, gallery, content);
        gallery.increaseReportCount();
        imageReportRepository.save(imageReport);
    }

    public boolean hasUserReported(Long galleryId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));

        return imageReportRepository.existsByMemberAndGallery(member, gallery);
    }

    public Long countReportsForGallery(Long galleryId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));
        return (long)gallery.getReportCount();
    }

    @Transactional
    public void updateGalleryBlindStatus(Long galleryId, boolean isBlinded) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));

        gallery.setBlinded(isBlinded);
        galleryRepository.save(gallery);
    }
}