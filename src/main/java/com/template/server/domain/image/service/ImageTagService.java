package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.ImageTagDto;
import com.template.server.domain.image.entity.ImageTag;
import com.template.server.domain.image.repository.ImageTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageTagService {

    private final ImageTagRepository imageTagRepository;

    @Transactional
    public List<ImageTag> getOrCreateTags(String[] tagNames) {
        List<ImageTag> tags = new ArrayList<>();

        for (String tagName : tagNames) {
            ImageTag tag = imageTagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        ImageTag newTag = ImageTag.create(tagName);
                        return imageTagRepository.save(newTag);
                    });
            tags.add(tag);
        }

        return tags;
    }

    @Transactional(readOnly = true)
    public Page<ImageTagDto> getAllTags(Pageable pageable) {
        return imageTagRepository.findAll(pageable).map(ImageTagDto::from);
    }

}
