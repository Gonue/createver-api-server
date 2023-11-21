package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.ImageTagDto;
import com.template.server.domain.image.entity.ImageTag;
import com.template.server.domain.image.repository.tag.ImageTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageTagService {

    private final ImageTagRepository imageTagRepository;

    @Transactional
    public List<ImageTag> getOrCreateTags(String[] tagNames) {
        List<ImageTag> tags = new ArrayList<>();
        Set<String> tagNameSet = new HashSet<>(Arrays.asList(tagNames));

        List<ImageTag> existingTags = imageTagRepository.findByNameIn(tagNameSet);

        Map<String, ImageTag> tagMap = existingTags.stream()
                                                    .collect(Collectors.toMap(ImageTag::getName, Function.identity()));

        for (String tagName : tagNames) {
            ImageTag tag = tagMap.get(tagName);
            if (tag == null) {
                tag = ImageTag.builder().name(tagName).build();
                tag = imageTagRepository.save(tag);
            }
            tags.add(tag);
        }

        return tags;
    }

    @Transactional(readOnly = true)
    public Page<ImageTagDto> getAllTags(Pageable pageable) {
        return imageTagRepository.findAll(pageable).map(ImageTagDto::from);
    }

}
