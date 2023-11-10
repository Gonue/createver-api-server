package com.template.server.domain.image.controller;


import com.template.server.domain.image.service.ImageReportService;
import com.template.server.global.error.response.Response;
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
                                    @RequestBody String content,
                                    Authentication authentication) {
        imageReportService.addReport(galleryId, authentication.getName(), content);
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
}
