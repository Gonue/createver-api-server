package com.createver.server.domain.image.repository.gallery;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.global.config.JpaConfig;
import com.createver.server.global.config.TestQuerydslConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Gallery Repository 테스트")
@DataJpaTest
@Import({TestQuerydslConfiguration.class, JpaConfig.class})
class CustomGalleryRepositoryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GalleryRepository galleryRepository;

    private Member testMember;
    private Gallery testGallery;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@gmail.com")
                .nickName("test")
                .profileImage("test.url")
                .password("test")
                .build();
        testMember = entityManager.persistFlushFind(testMember);

        ImageTag testTag = ImageTag.builder().name("testTag").build();
        testTag = entityManager.persistFlushFind(testTag);

        testGallery = Gallery.builder()
                .prompt("Gallery Prompt")
                .storageUrl("http://storage.url")
                .option(1)
                .tags(Collections.singletonList(testTag))
                .member(testMember)
                .build();
        testGallery = entityManager.persistFlushFind(testGallery);
    }


    @Test
    @DisplayName("특정 이메일로 갤러리 조회 및 정렬")
    void findGalleryByMemberEmailWithSortTest() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);

        Page<Gallery> result = galleryRepository.findGalleryByMemberEmail(testMember.getEmail(), pageable);

        assertFalse(result.isEmpty());
        assertNotNull(result.getContent().get(0).getCreatedAt());

        List<Gallery> galleries = result.getContent();
        for (int i = 0; i < galleries.size() - 1; i++) {
            assertTrue(galleries.get(i).getCreatedAt().isBefore(galleries.get(i + 1).getCreatedAt())
                    || galleries.get(i).getCreatedAt().isEqual(galleries.get(i + 1).getCreatedAt()));
        }
    }

    @Test
    @DisplayName("태그 이름으로 갤러리 조회 테스트")
    void findGalleryByTagsNameTest() {
        String tagName = "testTag";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Gallery> result = galleryRepository.findGalleryByTagsName(tagName, pageable);

        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream()
                .anyMatch(gallery -> gallery.getTags().stream()
                        .anyMatch(tag -> tag.getName().equals(tagName))));
    }

    @Test
    @DisplayName("갤러리 ID로 락을 걸고 조회 테스트")
    void findGalleryByIdForUpdateTest() {
        Optional<Gallery> foundGallery = galleryRepository.findGalleryByIdForUpdate(testGallery.getGalleryId());

        assertTrue(foundGallery.isPresent());
        assertEquals(testGallery.getGalleryId(), foundGallery.get().getGalleryId());
    }

    @Test
    @DisplayName("모든 갤러리에 대한 댓글 수와 함께 조회 및 정렬 테스트")
    void findAllGalleriesWithCommentAndSortTest() {
        Sort sort = Sort.by(Sort.Direction.ASC, "likeCount");
        Pageable pageable = PageRequest.of(0, 10, sort);

        Page<GalleryDto> result = galleryRepository.findAllGalleriesWithComment(pageable);

        assertFalse(result.isEmpty());
        assertNotNull(result.getContent().get(0).getLikeCount());

        List<GalleryDto> galleries = result.getContent();
        for (int i = 0; i < galleries.size() - 1; i++) {
            assertTrue(galleries.get(i).getLikeCount() <= galleries.get(i + 1).getLikeCount());
        }
    }
    @Test
    @DisplayName("옵션 및 프롬프트를 포함한 갤러리 조회 테스트")
    void hasOptionsAndPromptTest() {
        List<Integer> options = Collections.singletonList(1);
        String prompt = "Gallery Prompt";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Gallery> result = galleryRepository.hasOptionsAndPrompt(options, prompt, pageable);

        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream()
                .anyMatch(gallery -> gallery.getOption() == 1 && gallery.getPrompt().contains(prompt)));
    }


}