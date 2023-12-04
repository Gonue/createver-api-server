package com.createver.server.domain.image.entity;


import com.createver.server.global.audit.AuditingFields;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "image_tag")
@Entity
public class ImageTag extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_tag_id", updatable = false)
    private Long tagId;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Gallery> galleries = new ArrayList<>();

    @Builder
    public ImageTag(String name){
        this.name = name;
    }

}
