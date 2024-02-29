package com.createver.server.domain.music.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.music.entity.Music;
import com.createver.server.domain.music.repository.music.MusicRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final MusicRepository musicRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveMusicDetails(String prompt, String predictionId, Member member){
        Music music = Music.builder()
                .prompt(prompt)
                .status("processing")
                .predictionId(predictionId)
                .member(member)
                .build();

        musicRepository.save(music);
    }

    @Transactional
    public void deleteMusic(String email, Long musicId) {
        Member member = findMemberByEmail(email);
        Music music = findMusicOrThrow(musicId);
        checkMusicMember(music, member, email, musicId);
        musicRepository.delete(music);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 을 찾을수 없음", email)));
    }

    private void checkMusicMember(Music music, Member member, String email, Long articleId) {
        if (!Objects.equals(music.getMember().getMemberId(), member.getMemberId())) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION, String.format("%s 는 %s 의 권한이 없습니다.", email, articleId));
        }
    }

    private Music findMusicOrThrow(Long musicId) {
        return musicRepository.findById(musicId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MUSIC_NOT_FOUND));
    }
}
