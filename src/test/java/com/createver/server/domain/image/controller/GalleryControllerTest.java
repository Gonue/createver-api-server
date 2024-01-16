package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.service.GalleryService;
import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
import com.createver.server.global.util.aws.service.S3DownloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("Gallery Controller 테스트")
@WebMvcTest(controllers = GalleryController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class GalleryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GalleryService galleryService;

    @MockBean
    private S3DownloadService s3DownloadService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("갤러리 목록 조회 테스트")
    @WithMockCustomMember
    void testGalleryListWithCommentCountAndLikeCount() throws Exception {
        //given

        List<GalleryDto> galleries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GalleryDto galleryDto = new GalleryDto(
                    i + 1L,
                    "Test Prompt" + i,
                    "http://storage.url",
                    0,
                    LocalDateTime.now(),
                    5L,
                    10 + i,
                    2 + i,
                    1,
                    false);
            galleries.add(galleryDto);
        }
        Page<GalleryDto> page = new PageImpl<>(galleries, PageRequest.of(0, 20), 1);
        when(galleryService.galleryListWithComment(any(Pageable.class))).thenReturn(page);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/list/gallery")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        //then
        actions
                .andExpect(status().isOk())
                .andDo(document("gallery-list",
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
                                subsectionWithPath("result").description("페이징 처리된 갤러리 목록과 관련된 결과 데이터"),
                                subsectionWithPath("result.content[]").description("갤러리 목록"),
                                fieldWithPath("result.content[].galleryId").description("갤러리 ID"),
                                fieldWithPath("result.content[].prompt").description("프롬프트"),
                                fieldWithPath("result.content[].storageUrl").description("이미지 URL"),
                                fieldWithPath("result.content[].option").description("옵션"),
                                fieldWithPath("result.content[].createdAt").description("생성 시간"),
                                fieldWithPath("result.content[].commentCount").description("댓글 수"),
                                fieldWithPath("result.content[].likeCount").description("좋아요 수"),
                                fieldWithPath("result.content[].downloadCount").description("다운로드 수"),
                                fieldWithPath("result.content[].reportCount").description("신고 수"),
                                fieldWithPath("result.content[].blinded").description("블라인드 여부"),
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

    @Test
    @DisplayName("관리자용 갤러리 목록 조회 테스트")
    @WithMockCustomMember
    void testAdminGalleryList() throws Exception {
        // given
        List<GalleryDto> galleries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GalleryDto galleryDto = new GalleryDto(
                    i + 1L,
                    "Test Prompt" + i,
                    "http://storage.url",
                    0,
                    LocalDateTime.now(),
                    5L,
                    10 + i,
                    2 + i,
                    1,
                    false);
            galleries.add(galleryDto);
        }
        Page<GalleryDto> page = new PageImpl<>(galleries, PageRequest.of(0, 20), 1);
        when(galleryService.adminGalleryList(any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/admin/list/gallery")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "admin-gallery-list",
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
                                subsectionWithPath("result").description("페이징 처리된 갤러리 목록과 관련된 결과 데이터"),
                                subsectionWithPath("result.content[]").description("갤러리 목록"),
                                fieldWithPath("result.content[].galleryId").description("갤러리 ID"),
                                fieldWithPath("result.content[].prompt").description("프롬프트"),
                                fieldWithPath("result.content[].storageUrl").description("이미지 URL"),
                                fieldWithPath("result.content[].option").description("옵션"),
                                fieldWithPath("result.content[].createdAt").description("생성 시간"),
                                fieldWithPath("result.content[].commentCount").description("댓글 수"),
                                fieldWithPath("result.content[].likeCount").description("좋아요 수"),
                                fieldWithPath("result.content[].downloadCount").description("다운로드 수"),
                                fieldWithPath("result.content[].reportCount").description("신고 수"),
                                fieldWithPath("result.content[].blinded").description("블라인드 여부"),
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

    @Test
    @DisplayName("갤러리 검색 테스트")
    @WithMockCustomMember
    void testFindGalleryList() throws Exception {
        //given
        List<GalleryDto> galleries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            galleries.add(new GalleryDto(
                    i + 1L,
                    "Test Prompt" + i,
                    "http://storage.url",
                    0,
                    LocalDateTime.now(),
                    5L,
                    10 + i,
                    2 + i,
                    1,
                    false));
        }
        Page<GalleryDto> page = new PageImpl<>(galleries, PageRequest.of(0, 20), 1);
        when(galleryService.findGalleryListByOptionsAndPrompt(any(), anyString(), any(Pageable.class))).thenReturn(page);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/list/search")
                        .param("prompt", "Test Prompt")
                        .param("options", "1", "2")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        //then
        actions
                .andExpect(status().isOk())
                .andDo(document("gallery-search",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("prompt").description("검색 프롬프트"),
                                parameterWithName("options").description("검색 옵션 (선택적)"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc], [title, asc])")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("페이징 처리된 갤러리 목록과 관련된 결과 데이터"),
                                subsectionWithPath("result.content[]").description("갤러리 목록"),
                                fieldWithPath("result.content[].galleryId").description("갤러리 ID"),
                                fieldWithPath("result.content[].prompt").description("프롬프트"),
                                fieldWithPath("result.content[].storageUrl").description("이미지 URL"),
                                fieldWithPath("result.content[].option").description("옵션"),
                                fieldWithPath("result.content[].createdAt").description("생성 시간"),
                                fieldWithPath("result.content[].commentCount").description("댓글 수"),
                                fieldWithPath("result.content[].likeCount").description("좋아요 수"),
                                fieldWithPath("result.content[].downloadCount").description("다운로드 수"),
                                fieldWithPath("result.content[].reportCount").description("신고 수"),
                                fieldWithPath("result.content[].blinded").description("블라인드 여부"),
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

    @Test
    @DisplayName("태그별 갤러리 목록 조회 테스트")
    @WithMockCustomMember
    void testFindGalleryListByTag() throws Exception {
        //given
        List<GalleryDto> galleries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            galleries.add(new GalleryDto(
                    i + 1L,
                    "Test Prompt" + i,
                    "http://storage.url",
                    0, LocalDateTime.now(),
                    5L,
                    10 + i,
                    2 + i,
                    1,
                    false));
        }
        Page<GalleryDto> page = new PageImpl<>(galleries, PageRequest.of(0, 20), 1);
        when(galleryService.getGalleriesByTagName(anyString(), any(Pageable.class))).thenReturn(page);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/list/tag")
                        .param("tagName", "Test Tag")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        //then
        actions
                .andExpect(status().isOk())
                .andDo(document("gallery-tag-search",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("tagName").description("검색할 태그 이름"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc], [title, asc])")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("페이징 처리된 갤러리 목록과 관련된 결과 데이터"),
                                subsectionWithPath("result.content[]").description("갤러리 목록"),
                                fieldWithPath("result.content[].galleryId").description("갤러리 ID"),
                                fieldWithPath("result.content[].prompt").description("프롬프트"),
                                fieldWithPath("result.content[].storageUrl").description("이미지 URL"),
                                fieldWithPath("result.content[].option").description("옵션"),
                                fieldWithPath("result.content[].createdAt").description("생성 시간"),
                                fieldWithPath("result.content[].commentCount").description("댓글 수"),
                                fieldWithPath("result.content[].likeCount").description("좋아요 수"),
                                fieldWithPath("result.content[].downloadCount").description("다운로드 수"),
                                fieldWithPath("result.content[].reportCount").description("신고 수"),
                                fieldWithPath("result.content[].blinded").description("블라인드 여부"),
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

    @Test
    @DisplayName("갤러리 이미지 다운로드 테스트")
    @WithMockCustomMember
    void testDownloadImage() throws Exception {
        //given
        byte[] mockImageData = new byte[1024];
        Arrays.fill(mockImageData, (byte) 0);
        when(s3DownloadService.downloadFileByGalleryId(anyLong())).thenReturn(mockImageData);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/download/{galleryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE))
                .andDo(document("gallery-image-download",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("다운로드할 갤러리의 ID")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입 (이미지)"),
                                headerWithName(HttpHeaders.CONTENT_LENGTH).description("컨텐츠 길이")
                        )
                ));
    }

}