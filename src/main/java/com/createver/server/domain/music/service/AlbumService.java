package com.createver.server.domain.music.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.music.dto.AlbumDto;
import com.createver.server.domain.music.entity.Album;
import com.createver.server.domain.music.repository.album.AlbumRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void createAlbum(String email, String title, String imageUrl, String musicUrl){
        Member member = findMemberByEmail(email);
        Album album = Album.builder()
                .title(title)
                .imageUrl(imageUrl)
                .musicUrl(musicUrl)
                .member(member)
                .build();
        albumRepository.save(album);
    }

    @Transactional(readOnly = true)
    public AlbumDto findAlbumById(Long albumId){
        Album album = findAlbum(albumId);
        return AlbumDto.from(album);
    }

    @Transactional(readOnly = true)
    public Page<AlbumDto> albumList(Pageable pageable){
        return albumRepository.findAll(pageable).map(AlbumDto::from);
    }

    @Transactional
    public AlbumDto updateAlbum(String email, Long albumId, String title, String imageUrl, String musicUrl){
        Member member = findMemberByEmail(email);
        Album album = findAlbum(albumId);
        checkAlbumMember(album, member, email, albumId);
        album.updateAlbum(title, imageUrl, musicUrl);
        return AlbumDto.from(albumRepository.save(album));
    }

    @Transactional
    public void deleteAlbum(String email, Long albumId){
        Member member = findMemberByEmail(email);
        Album album = findAlbum(albumId);
        checkAlbumMember(album, member, email, albumId);
        albumRepository.delete(album);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 을 찾을수 없음", email)));
    }

    private Album findAlbum(Long albumId){
        return albumRepository.findById(albumId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ALBUM_NOT_FOUND, String.format("$s 을 찾을 수 없음", albumId)));
    }

    private void checkAlbumMember(Album album, Member member, String email, Long albumId) {
        if (!Objects.equals(album.getMember().getMemberId(), member.getMemberId())) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION, String.format("%s 는 %s 의 권한이 없습니다.", email, albumId));
        }
    }
}
