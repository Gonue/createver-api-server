package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.ImageTagDto;
import com.createver.server.domain.image.service.ImageTagService;
import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Image Tag Controller 테스트")
@WebMvcTest(controllers = ImageTagController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class ImageTagControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageTagService imageTagService;

    @Test
    @DisplayName("모든 이미지 태그 조회 테스트")
    @WithMockCustomMember
    void getAllTagsTest() throws Exception {
        // given
        List<ImageTagDto> tagDtoList = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            ImageTagDto imageTagDto = new ImageTagDto(
                    i + 1L,
                    "Test Tag" + i
            );
            tagDtoList.add(imageTagDto);
        }
        Page<ImageTagDto> page = new PageImpl<>(tagDtoList, PageRequest.of(0, 10), 1);
        when(imageTagService.getAllTags(any())).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/tags")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                                "get-all-tags",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기"),
                                        parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc]")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        subsectionWithPath("result").description("페이징 처리된 태그 목록과 관련된 결과 데이터"),
                                        subsectionWithPath("result.content[]").description("태그 목록"),
                                        fieldWithPath("result.content[].tagId").description("태그 ID"),
                                        fieldWithPath("result.content[].name").description("태그 이름"),
                                        subsectionWithPath("result.pageable").description("페이지 정보"),
                                        fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("result.totalElements").description("전체 게시글 수"),
                                        fieldWithPath("result.size").description("페이지당 게시글 수"),
                                        fieldWithPath("result.number").description("현재 페이지 번호"),
                                        subsectionWithPath("result.sort").description("정렬 정보"),
                                        fieldWithPath("result.numberOfElements").description("현재 페이지의 게시글 수"),
                                        fieldWithPath("result.empty").description("결과가 비어 있는지 여부")
                                )
                        )

                );
    }
}