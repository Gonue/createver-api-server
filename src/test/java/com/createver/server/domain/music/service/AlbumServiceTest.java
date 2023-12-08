package com.createver.server.domain.music.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.music.dto.AlbumDto;
import com.createver.server.domain.music.entity.Album;
import com.createver.server.domain.music.repository.album.AlbumRepository;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @InjectMocks
    private AlbumService albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member mockMember;
    private Album mockAlbum;

    @BeforeEach
    void setUp() {
        mockMember = Member.builder().email("test@example.com").profileImage("url").build();
        mockAlbum = Album.builder()
                .title("Test Album")
                .imageUrl("testImageUrl")
                .musicUrl("testMusicUrl")
                .member(mockMember)
                .build();
    }

    @DisplayName("앨범 생성 테스트")
    @Test
    void createAlbumTest() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mockMember));
        albumService.createAlbum("test@example.com", "Test Album", "imageUrl", "musicUrl");
        verify(albumRepository, times(1)).save(any(Album.class));
    }

    @DisplayName("앨범 단일 조회 테스트")
    @Test
    void findAlbumByIdTest() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(mockAlbum));
        AlbumDto result = albumService.findAlbumById(1L);
        assertNotNull(result);
        assertEquals("Test Album", result.getTitle());
    }


    @DisplayName("앨범 목록 조회 테스트")
    @Test
    void albumListTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Album> page = new PageImpl<>(Collections.singletonList(mockAlbum));
        when(albumRepository.findAll(pageable)).thenReturn(page);
        Page<AlbumDto> result = albumService.albumList(pageable);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @DisplayName("앨범 업데이트 테스트")
    @Test
    void updateAlbumTest() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mockMember));
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(mockAlbum));
        when(albumRepository.save(any(Album.class))).thenReturn(mockAlbum);
        AlbumDto result = albumService.updateAlbum("test@example.com", 1L, "Updated Album", "newImageUrl", "newMusicUrl");
        assertNotNull(result);
        assertEquals("Updated Album", result.getTitle());
    }

    @DisplayName("앨범 삭제 테스트")
    @Test
    void deleteAlbumTest() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mockMember));
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(mockAlbum));
        albumService.deleteAlbum("test@example.com", 1L);
        verify(albumRepository, times(1)).delete(any(Album.class));
    }

    @DisplayName("이메일로 회원 찾기 실패 시 예외 발생 테스트")
    @Test
    void findMemberByEmail_NotFound_ThrowsException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(BusinessLogicException.class, () -> albumService.createAlbum("nonexistent@example.com", "Test Album", "imageUrl", "musicUrl"));
    }

    @DisplayName("ID로 앨범 찾기 실패 시 예외 발생 테스트")
    @Test
    void findAlbum_NotFound_ThrowsException() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BusinessLogicException.class, () -> albumService.findAlbumById(999L));
    }

    @DisplayName("앨범 소유권 검증 실패 시 예외 발생 테스트")
    @Test
    void checkAlbumMember_InvalidOwner_ThrowsException() {
        // 다른 memberId를 가진 Member 모킹
        Member otherMember = mock(Member.class);
        when(otherMember.getMemberId()).thenReturn(2L);

        Member originalMember = mock(Member.class);
        when(originalMember.getMemberId()).thenReturn(1L);

        // Album 모킹, 원래 Member를 반환하도록 설정
        Album album = mock(Album.class);
        when(album.getMember()).thenReturn(originalMember);

        when(memberRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherMember));
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        assertThrows(BusinessLogicException.class, () -> albumService.deleteAlbum("other@example.com", 1L));
    }

}