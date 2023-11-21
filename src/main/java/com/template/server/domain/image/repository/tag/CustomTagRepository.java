package com.template.server.domain.image.repository.tag;

import com.template.server.domain.image.entity.ImageTag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CustomTagRepository {

    Optional<ImageTag> findByName(String name);
    List<ImageTag> findByNameIn(Set<String> names);

}
