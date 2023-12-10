package com.createver.server.domain.music.controller;

import com.createver.server.domain.article.controller.ArticleController;
import com.createver.server.domain.article.dto.ArticleDto;
import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.member.entity.PlanType;
import com.createver.server.domain.music.dto.AlbumDto;
import com.createver.server.domain.music.dto.request.AlbumCreateRequest;
import com.createver.server.domain.music.dto.request.AlbumUpdateRequest;
import com.createver.server.domain.music.service.AlbumService;
import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Album Controller 테스트")
@WebMvcTest(controllers = AlbumController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("앨범 생성 테스트")
    @WithMockCustomMember
    void createAlbumTest() throws Exception {
        //given
        AlbumCreateRequest createRequest = new AlbumCreateRequest("새 앨범", "이미지 URL", "음악 URL");
        String content = objectMapper.writeValueAsString(createRequest);

        //when

        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/music")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf().asHeader())
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andDo(document(
                                "create-album",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("title").description("앨범 제목"),
                                        fieldWithPath("imageUrl").description("앨범 커버 이미지 URL"),
                                        fieldWithPath("musicUrl").description("음악 파일 URL")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result").description("결과 (null)")
                                )
                        )
                );
    }

    @Test
    @DisplayName("앨범 업데이트 테스트")
    @WithMockCustomMember
    void updateAlbumTest() throws Exception {
        // given
        Long albumId = 1L;
        AlbumUpdateRequest request = new AlbumUpdateRequest("Updated Title", "Updated Image URL", "Updated Music URL");
        String content = objectMapper.writeValueAsString(request);

        MemberDto mockMember = new MemberDto(1L, "test@example.com", "nickname", "password", "profileImageUrl", LocalDateTime.now(), LocalDateTime.now(), null);

        AlbumDto albumDto = new AlbumDto(
                albumId,
                request.getTitle(),
                request.getImageUrl(),
                request.getMusicUrl(),
                mockMember,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(albumService.updateAlbum(anyString(), anyLong(), anyString(), anyString(), anyString())).thenReturn(albumDto);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/music/{albumId}", albumId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "update-album",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("albumId").description("업데이트할 앨범의 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("업데이트할 앨범 제목"),
                                fieldWithPath("imageUrl").description("업데이트할 앨범 이미지 URL"),
                                fieldWithPath("musicUrl").description("업데이트할 음악 파일 URL")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.albumId").description("앨범 ID"),
                                fieldWithPath("result.title").description("앨범 제목"),
                                fieldWithPath("result.imageUrl").description("앨범 커버 URL"),
                                fieldWithPath("result.musicUrl").description("앨범 음악 URL"),
                                fieldWithPath("result.createdAt").description("생성 시간"),
                                fieldWithPath("result.modifiedAt").description("수정 시간"),
                                fieldWithPath("result.member.memberId").description("멤버 ID"),
                                fieldWithPath("result.member.email").description("멤버 이메일"),
                                fieldWithPath("result.member.nickName").description("멤버 닉네임"),
                                fieldWithPath("result.member.profileImage").description("멤버 프로필 이미지"),
                                fieldWithPath("result.member.planType").description("멤버 플랜 타입")
                        )
                ));
    }

    @Test
    @DisplayName("앨범 삭제 테스트")
    @WithMockCustomMember
    void deleteAlbumTest() throws Exception {
        // given

        Long albumId = 1L;
        doNothing().when(albumService).deleteAlbum(anyString(), anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/music/{albumId}", albumId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                                "delete-album",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("albumId").description("삭제할 앨범의 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result").description("결과 (null)")
                                )
                        )
                );
    }

    @Test
    @DisplayName("앨범 ID로 조회 테스트")
    @WithMockCustomMember
    void findAlbumByIdTest() throws Exception {
        // given

        Long albumId = 1L;
        MemberDto mockMember = new MemberDto(1L, "test@example.com", "nickname", "password", "profileImageUrl", LocalDateTime.now(), LocalDateTime.now(), null);
        AlbumDto albumDto = new AlbumDto(
                albumId,
                "title",
                "imageUrl",
                "musicUrl",
                mockMember,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(albumService.findAlbumById(1L)).thenReturn(albumDto);


        ResultActions actions = mockMvc.perform(
                get("/api/v1/music/{albumId}", albumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        actions
                .andExpect(status().isOk())
                .andDo(document("find-album-by-id",
                                pathParameters(
                                        parameterWithName("albumId").description("조회할 앨범의 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result.albumId").description("앨범 ID"),
                                        fieldWithPath("result.title").description("앨범 제목"),
                                        fieldWithPath("result.imageUrl").description("앨범 커버 URL"),
                                        fieldWithPath("result.musicUrl").description("앨범 음악 URL"),
                                        fieldWithPath("result.createdAt").description("생성 시간"),
                                        fieldWithPath("result.modifiedAt").description("수정 시간"),
                                        fieldWithPath("result.member.memberId").description("멤버 ID"),
                                        fieldWithPath("result.member.email").description("멤버 이메일"),
                                        fieldWithPath("result.member.nickName").description("멤버 닉네임"),
                                        fieldWithPath("result.member.profileImage").description("멤버 프로필 이미지"),
                                        fieldWithPath("result.member.planType").description("멤버 플랜 타입")
                                )
                        )
                );
    }

    @Test
    @DisplayName("앨범 목록 조회 테스트")
    @WithMockCustomMember
    void albumListTest() throws Exception {
        // given
        List<AlbumDto> albumDtoList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AlbumDto albumDto = new AlbumDto(
                    i + 1L,
                    "title" + i,
                    "imageUrl" + i,
                    "musicUrl" + i,
                    new MemberDto(i + 1L, "test" + i + "@example.com", "김테스트", "password", "imageUrl", LocalDateTime.now(), LocalDateTime.now(), null),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            albumDtoList.add(albumDto);
        }


        Page<AlbumDto> page = new PageImpl<>(albumDtoList, PageRequest.of(0, 20), 1);
        when(albumService.albumList(any(Pageable.class))).thenReturn(page);


        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/music")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "album-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc], [title, asc])")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("페이징 처리된 앨범 목록과 관련된 데이터"),
                                fieldWithPath("result.content[].albumId").description("앨범 ID"),
                                fieldWithPath("result.content[].title").description("앨범 제목"),
                                fieldWithPath("result.content[].imageUrl").description("앨범 커버 URL"),
                                fieldWithPath("result.content[].musicUrl").description("앨범 음악 URL"),
                                fieldWithPath("result.content[].createdAt").description("생성 시간"),
                                fieldWithPath("result.content[].modifiedAt").description("수정 시간"),
                                subsectionWithPath("result.content[].member").description("앨범 생성자 정보"),
                                subsectionWithPath("result.pageable").description("페이지 정보"),
                                fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                fieldWithPath("result.totalElements").description("전체 게시글 수"),
                                fieldWithPath("result.size").description("페이지당 게시글 수"),
                                fieldWithPath("result.number").description("현재 페이지 번호"),
                                subsectionWithPath("result.sort").description("정렬 정보"),
                                fieldWithPath("result.numberOfElements").description("현재 페이지의 게시글 수"),
                                fieldWithPath("result.empty").description("결과가 비어 있는지 여부")
                        )
                ));
    }


}