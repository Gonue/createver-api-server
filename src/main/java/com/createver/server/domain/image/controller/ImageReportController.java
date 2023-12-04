package com.createver.server.domain.image.controller;


import com.createver.server.domain.image.dto.request.ImageReportRequest;
import com.createver.server.domain.image.service.ImageReportService;
import com.createver.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageReportController {

    private final ImageReportService imageReportService;

    @PostMapping("/report/{galleryId}")
    public Response<Void> addReport(@PathVariable Long galleryId,
                                    @RequestBody @Valid ImageReportRequest request,
                                    Authentication authentication) {
        imageReportService.addReport(galleryId, authentication.getName(), request.getContent());
        return Response.success();
    }

    @GetMapping("/report/count/{galleryId}")
    public Response<Long> countReports(@PathVariable Long galleryId) {
        Long count = imageReportService.countReportsForGallery(galleryId);
        return Response.success(200, count);
    }

    @GetMapping("/report/status/{galleryId}")
    public Response<Boolean> checkUserReportStatus(@PathVariable Long galleryId,
                                                   Authentication authentication) {
        boolean hasReported = imageReportService.hasUserReported(galleryId, authentication.getName());
        return Response.success(200, hasReported);
    }

    @PatchMapping("/admin/gallery/{galleryId}/blind")
    public Response<Void> updateGalleryBlindStatus(@PathVariable Long galleryId,
                                                   @RequestParam boolean isBlinded) {
        imageReportService.updateGalleryBlindStatus(galleryId, isBlinded);
        return Response.success();
    }
}
