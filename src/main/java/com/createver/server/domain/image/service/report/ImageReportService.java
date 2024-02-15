package com.createver.server.domain.image.service.report;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageReport;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.image.repository.report.ImageReportRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

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

        ImageReport imageReport = ImageReport.
                builder()
                .member(member)
                .gallery(gallery)
                .content(content)
                .build();
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
        return (long) gallery.getReportCount();
    }

    @Transactional
    public void updateGalleryBlindStatus(Long galleryId, boolean isBlinded) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.GALLERY_NOT_FOUND));

        gallery.updateBlindStatus(isBlinded);
        galleryRepository.save(gallery);
    }
}