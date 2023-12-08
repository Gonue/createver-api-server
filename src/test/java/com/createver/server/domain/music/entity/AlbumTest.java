package com.createver.server.domain.music.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Album Entity 테스트")
class AlbumTest {

    @DisplayName("Album 생성 테스트")
    @Test
    void testAlbumCreation() {
        // Given
        String title = "Album Title";
        String imageUrl = "image.jpg";
        String musicUrl = "music.mp3";
        Member mockMember = mock(Member.class);

        // When
        Album album = Album.builder()
                .title(title)
                .imageUrl(imageUrl)
                .musicUrl(musicUrl)
                .member(mockMember)
                .build();

        // Then
        assertEquals(title, album.getTitle());
        assertEquals(imageUrl, album.getImageUrl());
        assertEquals(musicUrl, album.getMusicUrl());
        assertEquals(mockMember, album.getMember());
    }

    @DisplayName("Album 업데이트 테스트")
    @Test
    void testUpdateAlbum() {
        // Given
        Album album = Album.builder()
                .title("Original Title")
                .imageUrl("original.jpg")
                .musicUrl("original.mp3")
                .member(mock(Member.class))
                .build();
        String updatedTitle = "Updated Title";
        String updatedImageUrl = "updated.jpg";
        String updatedMusicUrl = "updated.mp3";

        // When
        album.updateAlbum(updatedTitle, updatedImageUrl, updatedMusicUrl);

        // Then
        assertEquals(updatedTitle, album.getTitle());
        assertEquals(updatedImageUrl, album.getImageUrl());
        assertEquals(updatedMusicUrl, album.getMusicUrl());
    }
}
