package com.template.server.domain.image.entity;


import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
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

    public static ImageTag create(String name) {
        ImageTag tag = new ImageTag();
        tag.setName(name);
        return tag;
    }
}