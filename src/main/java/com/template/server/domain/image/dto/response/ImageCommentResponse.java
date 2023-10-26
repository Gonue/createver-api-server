package com.template.server.domain.image.dto.response;

import com.template.server.domain.image.dto.ImageCommentDto;
import com.template.server.domain.member.dto.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ImageCommentResponse {
    private Long imageCommentId;
    private Long galleryId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private MemberResponse member;


    public static ImageCommentResponse from(ImageCommentDto dto){
        return new ImageCommentResponse(
                dto.getImageCommentId(),
                dto.getGallery_id(),
                dto.getContent(),
                dto.getCreatedAt(),
                dto.getModifiedAt(),
                MemberResponse.from(dto.getMember())
        );
    }
}
