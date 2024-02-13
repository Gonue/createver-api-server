package com.createver.server.domain.banner.controller;

import com.createver.server.domain.banner.dto.BannerDto;
import com.createver.server.domain.banner.dto.request.BannerCreateRequest;
import com.createver.server.domain.banner.dto.request.BannerUpdateRequest;
import com.createver.server.domain.banner.dto.response.BannerResponse;
import com.createver.server.domain.banner.service.BannerService;
import com.createver.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/banner")
public class BannerController {

    private final BannerService bannerService;

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Void> createBanner(@RequestBody @Valid BannerCreateRequest request) {
        bannerService.createBanner(request);
        return Response.success(201, null);
    }

    @GetMapping("/list")
    public Response<Page<BannerResponse>> activeBannerList(Pageable pageable) {
        return Response.success(bannerService.activeBannerList(pageable).map(BannerResponse::from));
    }

    @GetMapping("/admin/list")
    public Response<Page<BannerResponse>> bannerList(Pageable pageable) {
        return Response.success(bannerService.bannerList(pageable).map(BannerResponse::from));
    }

    @PatchMapping("/admin/{bannerId}")
    public Response<BannerResponse> updateBanner(@PathVariable Long bannerId,
                                                 @RequestBody BannerUpdateRequest request) {
        BannerDto bannerDto = bannerService.updateBanner(request);
        return Response.success(200, BannerResponse.from(bannerDto));
    }

    @PatchMapping("/admin/order/{bannerId}")
    public Response<BannerResponse> updateBannerOrder(@PathVariable Long bannerId,
                                                      @RequestBody BannerUpdateRequest request) {
        BannerDto bannerDto = bannerService.updateBannerOrder(request);
        return Response.success(200, BannerResponse.from(bannerDto));
    }


    @DeleteMapping("/admin/{bannerId}")
    public Response<Void> deleteBanner(@PathVariable Long bannerId) {
        bannerService.deleteBanner(bannerId);
        return Response.success();
    }

}
