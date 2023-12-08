package com.createver.server.domain.image.repository.comment;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageComment;
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
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Image Comment Repository 테스트")
@DataJpaTest
@Import({TestQuerydslConfiguration.class, JpaConfig.class})
class CustomCommentRepositoryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ImageCommentRepository imageCommentRepository;

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

        testGallery = Gallery.builder()
                .prompt("Gallery Prompt")
                .storageUrl("http://storage.url")
                .option(1)
                .tags(Collections.emptyList())
                .member(testMember)
                .build();
        testGallery = entityManager.persistFlushFind(testGallery);

        ImageComment comment1 = ImageComment.builder()
                .content("Test Comment 1")
                .gallery(testGallery)
                .member(testMember)
                .build();
        entityManager.persist(comment1);

        ImageComment comment2 = ImageComment.builder()
                .content("Test Comment 2")
                .gallery(testGallery)
                .member(testMember)
                .build();
        entityManager.persist(comment2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("갤러리 ID로 댓글 조회 테스트")
    void findByGalleryIdTest() {
        Sort sort = Sort.by("createdAt").descending();
        PageRequest pageable = PageRequest.of(0, 10, sort);

        Page<ImageComment> result = imageCommentRepository.findByGalleryId(testGallery.getGalleryId(), pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
            .extracting(ImageComment::getContent)
            .containsExactly("Test Comment 2", "Test Comment 1");
    }
}