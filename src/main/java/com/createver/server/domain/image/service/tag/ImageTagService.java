package com.createver.server.domain.image.service.tag;

import com.createver.server.domain.image.dto.ImageTagDto;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.image.repository.tag.ImageTagRepository;
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

        Set<String> tagNameSet = Arrays.stream(tagNames)
                .map(tagName -> tagName.replace(",", "").replace(".", "")) // 쉼표와 마침표 제거
                .collect(Collectors.toSet());

        List<ImageTag> existingTags = new ArrayList<>(imageTagRepository.findByNameIn(tagNameSet));
        Map<String, ImageTag> tagMap = existingTags.stream()
                .collect(Collectors.toMap(ImageTag::getName, Function.identity()));

        List<ImageTag> newTags = new ArrayList<>();
        
        for (String tagName : tagNameSet) { // 중복 제거된 태그 순회
            if (!tagMap.containsKey(tagName)) {
                // 새 태그 생성 및 리스트에 추가
                ImageTag newTag = ImageTag.builder()
                        .name(tagName)
                        .build();
                newTags.add(newTag);
            }
        }
        // 새 태그들을 데이터베이스에 일괄 저장
        if (!newTags.isEmpty()) {
            imageTagRepository.saveAll(newTags);
            // 저장된 태그들을 기존 태그 목록에 추가
            existingTags.addAll(newTags);
        }

        return existingTags;
    }

    @Transactional(readOnly = true)
    public Page<ImageTagDto> getAllTags(Pageable pageable) {
        return imageTagRepository.findAll(pageable).map(ImageTagDto::from);
    }

}
