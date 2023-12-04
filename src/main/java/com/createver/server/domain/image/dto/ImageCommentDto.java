package com.createver.server.domain.image.dto;

import com.createver.server.domain.image.entity.ImageComment;
import com.createver.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ImageCommentDto {

    private Long imageCommentId;
    private String content;
    private Long gallery_id;
    private MemberDto member;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ImageCommentDto from(ImageComment entity){
        return new ImageCommentDto(
                entity.getCommentId(),
                entity.getContent(),
                entity.getGallery().getGalleryId(),
                MemberDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
