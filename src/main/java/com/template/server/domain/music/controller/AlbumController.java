package com.template.server.domain.music.controller;

import com.template.server.domain.music.dto.AlbumDto;
import com.template.server.domain.music.dto.request.AlbumCreateRequest;
import com.template.server.domain.music.dto.request.AlbumUpdateRequest;
import com.template.server.domain.music.dto.response.AlbumResponse;
import com.template.server.domain.music.service.AlbumService;
import com.template.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/music")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public Response<Void> createAlbum(@RequestBody @Valid AlbumCreateRequest request, Authentication authentication) {
        albumService.createAlbum(authentication.getName(), request.getTitle(), request.getImageUrl(), request.getMusicUrl());
        return Response.success(201, null);
    }

    @GetMapping("/{albumId}")
    public Response<AlbumResponse> findAlbumById(@PathVariable Long albumId){
        AlbumDto albumDto = albumService.findAlbumById(albumId);
        return Response.success(200, AlbumResponse.from(albumDto));
    }

    @GetMapping
    public Response<Page<AlbumResponse>> albumList(Pageable pageable){
        Page<AlbumDto> page = albumService.albumList(pageable);
        return Response.success(page.map(AlbumResponse::from));
    }

    @PatchMapping("/{albumId}")
    public Response<AlbumResponse> updateAlbum(@PathVariable Long albumId,
                                               @RequestBody @Valid AlbumUpdateRequest request,
                                               Authentication authentication){
        AlbumDto albumDto = albumService.updateAlbum(authentication.getName(), albumId, request.getTitle(), request.getImageUrl(), request.getMusicUrl());
        return Response.success(200, AlbumResponse.from(albumDto));
    }

    @DeleteMapping("/{albumId}")
    public Response<Void> deleteAlbum(@PathVariable Long albumId, Authentication authentication){
        albumService.deleteAlbum(authentication.getName(), albumId);
        return Response.success();
    }
}
