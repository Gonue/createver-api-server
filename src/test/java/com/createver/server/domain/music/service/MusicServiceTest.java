package com.createver.server.domain.music.service;


import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.music.entity.Music;
import com.createver.server.domain.music.repository.music.MusicRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Music Service 테스트")
@ExtendWith(MockitoExtension.class)
class MusicServiceTest {

    @InjectMocks
    private MusicService musicService;

    @Mock
    private MusicRepository musicRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("음악 세부 정보 저장 성공 테스트")
    @Test
    void saveMusicDetails_Success(){
        // Given
        String prompt = "테스트 프롬프트";
        String predictionId = "predictionId";
        Member testMember = Member.builder()
                .email("test@email.com")
                .build();
        // When
        musicService.saveMusicDetails(prompt, predictionId, testMember);

        // Then
        verify(musicRepository, times(1)).save(any(Music.class));

    }

    @DisplayName("음악 삭제 성공 테스트")
    @Test
    void deleteMusic_Success() {
        // Given
        Long musicId = 1L;
        String email = "test@email.com";
        Member testMember = mock(Member.class);
        Music testMusic = mock(Music.class);

        when(testMusic.getMember()).thenReturn(testMember);
        when(testMember.getMemberId()).thenReturn(1L);
        when(musicRepository.findById(musicId)).thenReturn(Optional.of(testMusic));
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(testMember));

        // When
        musicService.deleteMusic(email, musicId);

        // Then
        verify(musicRepository).delete(testMusic);
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회 시 예외 발생")
    void findMemberByEmailNotFoundTest() {
        // Given
        Long musicId = 1L;
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessLogicException.class, () -> musicService.deleteMusic(email, musicId));
    }

    @Test
    @DisplayName("Article 업데이트 권한 없는 사용자 시 예외 발생")
    void updateMusicInvalidPermissionTest() {
        // Given
        Long musicId = 1L;
        String differentEmail = "another@example.com";
        Member memberWithDifferentId = mock(Member.class);
        Member originalMember = mock(Member.class);
        Music music = mock(Music.class);

        when(memberWithDifferentId.getMemberId()).thenReturn(2L); // 다른 ID 반환
        when(originalMember.getMemberId()).thenReturn(1L); // 원래 Member의 ID 반환
        when(music.getMember()).thenReturn(originalMember); // 원래 Member 반환

        when(memberRepository.findByEmail(differentEmail)).thenReturn(Optional.of(memberWithDifferentId));
        when(musicRepository.findById(musicId)).thenReturn(Optional.of(music));

        // When & Then
        assertThrows(BusinessLogicException.class,
            () -> musicService.deleteMusic(differentEmail, musicId));
    }
}
