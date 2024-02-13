package com.createver.server.domain.banner.service;

import com.createver.server.domain.banner.dto.BannerDto;
import com.createver.server.domain.banner.dto.request.BannerCreateRequest;
import com.createver.server.domain.banner.dto.request.BannerUpdateRequest;
import com.createver.server.domain.banner.entity.Banner;
import com.createver.server.domain.banner.repository.BannerRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Banner Service 테스트")
@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

    @Mock
    private BannerRepository bannerRepository;

    @InjectMocks
    private BannerService bannerService;

    private Banner banner;
    private BannerCreateRequest createRequest;
    private BannerUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        banner = Banner.builder()
                .imageUrl("http://example.com/banner.jpg")
                .tags(List.of("Tag1", "Tag2"))
                .title("Test Title")
                .content("Test Content")
                .active(true)
                .position("top")
                .orderSequence(1)
                .build();

        createRequest = new BannerCreateRequest(
                "http://example.com/banner.jpg",
                List.of("Tag1", "Tag2"),
                "Test Title",
                "Test Content",
                true,
                "Top",
                1);

        updateRequest = new BannerUpdateRequest(
                1L,
                "http://example.com/banner_new.jpg",
                List.of("Tag1", "Tag2"),
                "Test Title",
                "Test Content",
                false,
                "main",
                2);
    }

    @Test
    @DisplayName("배너 생성 테스트")
    void createBannerTest() {
        // Given
        when(bannerRepository.save(any(Banner.class))).thenAnswer(invocation -> {
            Banner savedBanner = invocation.getArgument(0);
            return savedBanner;
        });

        // When
        bannerService.createBanner(createRequest);

        // Then
        verify(bannerRepository).save(any(Banner.class));
    }

    @Test
    @DisplayName("배너 목록 조회 테스트")
    void bannerListTest() {
        // Given
        Pageable pageable = Pageable.unpaged();
        when(bannerRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(banner)));

        // When
        Page<BannerDto> result = bannerService.bannerList(pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(bannerRepository).findAll(pageable);
    }

    @Test
    @DisplayName("활성화된 배너 목록 조회 테스트")
    void activeBannerListTest() {
        // Given
        Pageable pageable = Pageable.unpaged();
        when(bannerRepository.findByActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(banner)));

        // When
        Page<BannerDto> result = bannerService.activeBannerList(pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(bannerRepository).findByActiveTrue(pageable);
    }

    @Test
    @DisplayName("배너 수정 테스트")
    void updateBannerTest() {
        // Given
        when(bannerRepository.findById(anyLong())).thenReturn(Optional.of(banner));
        when(bannerRepository.save(any(Banner.class))).thenReturn(banner);

        // When
        BannerDto updatedBanner = bannerService.updateBanner(updateRequest);

        // Then
        assertNotNull(updatedBanner);
        verify(bannerRepository).save(any(Banner.class));
        verify(bannerRepository).findById(anyLong());
    }

    @Test
    @DisplayName("배너 순서 업데이트 테스트")
    void updateBannerOrderTest() {
        // Given
        long bannerId = 1L;
        int newOrderSequence = 2;
        BannerUpdateRequest updateOrderRequest = new BannerUpdateRequest(
                bannerId,
                banner.getImageUrl(),
                banner.getTags(),
                banner.getTitle(),
                banner.getContent(),
                banner.isActive(),
                banner.getPosition(),
                newOrderSequence);

        when(bannerRepository.findById(bannerId)).thenReturn(Optional.of(banner));
        when(bannerRepository.save(any(Banner.class))).then(returnsFirstArg());

        // When
        BannerDto updatedBannerDto = bannerService.updateBannerOrder(updateOrderRequest);

        // Then
        assertNotNull(updatedBannerDto);
        assertEquals(newOrderSequence, updatedBannerDto.getOrderSequence());
        verify(bannerRepository).save(banner);
        verify(bannerRepository).findById(bannerId);
    }

    @Test
    @DisplayName("존재하지 않는 배너 수정 시 예외 발생 테스트")
    void updateNonExistentBannerTest() {
        // Given
        when(bannerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessLogicException.class, () -> bannerService.updateBanner(updateRequest));
    }

    @Test
    @DisplayName("배너 삭제 테스트")
    void deleteBannerTest() {
        // Given
        when(bannerRepository.findById(anyLong())).thenReturn(Optional.of(banner));

        // When
        bannerService.deleteBanner(1L);

        // Then
        verify(bannerRepository).delete(any(Banner.class));
    }

    @Test
    @DisplayName("존재하지 않는 배너 삭제 시 예외 발생 테스트")
    void deleteNonExistentBannerTest() {
        // Given
        when(bannerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessLogicException.class, () -> bannerService.deleteBanner(999L));
    }
}
