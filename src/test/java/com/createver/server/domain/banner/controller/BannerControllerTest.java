package com.createver.server.domain.banner.controller;

import com.createver.server.domain.banner.dto.BannerDto;
import com.createver.server.domain.banner.dto.request.BannerCreateRequest;
import com.createver.server.domain.banner.dto.request.BannerUpdateRequest;
import com.createver.server.domain.banner.service.BannerService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Banner Controller 테스트")
@WebMvcTest(controllers = BannerController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class BannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BannerService bannerService;

    @Test
    @DisplayName("배너 생성 테스트")
    @WithMockCustomMember
    void createBannerTest() throws Exception {
        BannerCreateRequest request = new BannerCreateRequest("http://example.com/banner.jpg", List.of("Tag1", "Tag2"), "Test Title", "Test Content", true, "Top", 1);
        String content = objectMapper.writeValueAsString(request);

        ResultActions actions =
                mockMvc.perform(post("/api/v1/banner/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
                );

        actions
                .andExpect(status().isCreated())
                .andDo(document(
                        "create-banner",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("imageUrl").description("이미지 URL"),
                                fieldWithPath("tags").description("태그 이름"),
                                fieldWithPath("title").description("배너 제목"),
                                fieldWithPath("content").description("배너 내용"),
                                fieldWithPath("active").description("활성 상태"),
                                fieldWithPath("position").description("배너 위치"),
                                fieldWithPath("orderSequence").description("배너 순서")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과")
                        )
                ));
    }

    @Test
    @DisplayName("배너 수정")
    @WithMockCustomMember
    void updateBannerTest() throws Exception {
        Long bannerId = 1L;
        BannerUpdateRequest request = new BannerUpdateRequest(bannerId, "http://example.com/banner_new.jpg", List.of("Tag1", "Tag2"), "Test Title", "Test Content", false, "main", 2);
        String content = objectMapper.writeValueAsString(request);

        BannerDto bannerDto = new BannerDto(bannerId, request.getImageUrl(), request.getTags(), request.getTitle(), request.getContent(), request.getActive(), request.getPosition(), request.getOrderSequence(), LocalDateTime.now(), LocalDateTime.now());

        when(bannerService.updateBanner(any(BannerUpdateRequest.class))).thenReturn(bannerDto);

        ResultActions actions = mockMvc.perform(
                patch("/api/v1/banner/admin/{bannerId}", bannerId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // Then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "update-banner",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("bannerId").description("수정할 배너 ID")
                        ),
                        requestFields(
                                fieldWithPath("bannerId").description("배너 ID"),
                                fieldWithPath("imageUrl").description("이미지 URL"),
                                fieldWithPath("tags").description("태그 이름"),
                                fieldWithPath("title").description("배너 제목"),
                                fieldWithPath("content").description("배너 내용"),
                                fieldWithPath("active").description("활성 상태"),
                                fieldWithPath("position").description("배너 위치"),
                                fieldWithPath("orderSequence").description("배너 순서")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("응답 결과")
                        )
                ));
    }

    @Test
    @DisplayName("배너 삭제")
    @WithMockCustomMember
    void deleteBannerTest() throws Exception {
        Long bannerId = 1L;

        doNothing().when(bannerService).deleteBanner(anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/banner/admin/{bannerId}", bannerId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "delete-banner",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("bannerId").description("삭제할 배너 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과")
                        )
                ));
    }

    @Test
    @DisplayName("배너 순서 업데이트")
    @WithMockCustomMember
    void updateBannerOrderTest() throws Exception {
        // given
        Long bannerId = 1L;
        BannerUpdateRequest request = new BannerUpdateRequest(bannerId, "http://example.com/banner_new.jpg", List.of("Tag1Updated", "Tag2Updated"), "Updated Title", "Updated Content", true, "Bottom", 2);
        String content = objectMapper.writeValueAsString(request);

        BannerDto bannerDto = new BannerDto(bannerId, request.getImageUrl(), request.getTags(), request.getTitle(), request.getContent(), request.getActive(), request.getPosition(), request.getOrderSequence(), LocalDateTime.now(), LocalDateTime.now());
        when(bannerService.updateBannerOrder(any(BannerUpdateRequest.class))).thenReturn(bannerDto);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/banner/admin/order/{bannerId}", bannerId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "update-banner-order",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("bannerId").description("순서를 업데이트할 배너의 ID")
                        ),
                        requestFields(
                                fieldWithPath("bannerId").ignored(),
                                fieldWithPath("imageUrl").description("업데이트될 이미지 URL"),
                                fieldWithPath("tags").description("업데이트될 태그 배열"),
                                fieldWithPath("title").description("업데이트될 배너 제목"),
                                fieldWithPath("content").description("업데이트될 배너 내용"),
                                fieldWithPath("active").description("업데이트될 활성 상태"),
                                fieldWithPath("position").description("업데이트될 배너 위치"),
                                fieldWithPath("orderSequence").description("업데이트될 배너 순서")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("응답 결과 데이터")
                        )
                ));
    }

    @Test
    @DisplayName("활성 배너 목록 조회")
    @WithMockCustomMember
    void activeBannerListTest() throws Exception {
        // given
        List<BannerDto> banners = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            BannerDto bannerDto = new BannerDto(
                    1L, "http://example.com/banner.jpg", List.of("Tag1", "Tag2"), "Active Banner", "This is an active banner.", true, "Top", 1, LocalDateTime.now(), LocalDateTime.now()
            );
            banners.add(bannerDto);
        }
        Page<BannerDto> page = new PageImpl<>(banners, PageRequest.of(0, 10), 1);
        when(bannerService.activeBannerList(any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/banner/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "active-banner-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("응답 결과")
                        )
                ));
    }

    @Test
    @DisplayName("전체 배너 목록 조회")
    @WithMockCustomMember
    void bannerListTest() throws Exception {
        // given
        List<BannerDto> banners = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            BannerDto bannerDto = new BannerDto(
                    1L, "http://example.com/banner.jpg", List.of("Tag3", "Tag4"), "Active Banner", "This is an active banner.", true, "Top", 1, LocalDateTime.now(), LocalDateTime.now()
            );
            banners.add(bannerDto);
        }
        Page<BannerDto> page = new PageImpl<>(banners, PageRequest.of(0, 10), 1);
        when(bannerService.bannerList(any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/banner/admin/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "banner-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("응답 결과")
                        )
                ));
    }
}