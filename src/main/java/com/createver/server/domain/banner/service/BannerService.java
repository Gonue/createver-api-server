package com.createver.server.domain.banner.service;

import com.createver.server.domain.banner.dto.request.BannerCreateRequest;
import com.createver.server.domain.banner.dto.BannerDto;
import com.createver.server.domain.banner.dto.request.BannerUpdateRequest;
import com.createver.server.domain.banner.entity.Banner;
import com.createver.server.domain.banner.repository.BannerRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    @Transactional
    public void createBanner(BannerCreateRequest request) {
        Banner banner = Banner.builder()
                .imageUrl(request.getImageUrl())
                .tags(request.getTags())
                .title(request.getTitle())
                .content(request.getContent())
                .active(request.isActive())
                .position(request.getPosition())
                .orderSequence(request.getOrderSequence())
                .build();
        bannerRepository.save(banner);
    }

    @Transactional(readOnly = true)
    public Page<BannerDto> bannerList(Pageable pageable) {
        return bannerRepository.findAll(pageable).map(BannerDto::from);
    }

    @Transactional(readOnly = true)
    public Page<BannerDto> activeBannerList(Pageable pageable) {
        return bannerRepository.findByActiveTrue(pageable).map(BannerDto::from);
    }

    @Transactional
    public BannerDto updateBanner(BannerUpdateRequest request) {
        Banner banner = findBannerOrThrow(request.getBannerId());

        banner.updateBanner(
                request.getImageUrl(),
                request.getTags(),
                request.getTitle(),
                request.getContent(),
                request.getActive(),
                request.getPosition(),
                request.getOrderSequence()
        );
        return BannerDto.from(bannerRepository.save(banner));
    }

    @Transactional
    public BannerDto updateBannerOrder(BannerUpdateRequest request) {
        Banner banner = findBannerOrThrow(request.getBannerId());
        banner.updateOrder(request.getOrderSequence());
        return BannerDto.from(bannerRepository.save(banner));
    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner banner = findBannerOrThrow(bannerId);
        bannerRepository.delete(banner);
    }

    private Banner findBannerOrThrow(Long bannerId) {
        return bannerRepository.findById(bannerId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BANNER_NOT_FOUND, String.format("%s 을 찾을 수 없음", bannerId)));
    }
}
