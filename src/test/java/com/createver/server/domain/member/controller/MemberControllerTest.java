package com.createver.server.domain.member.controller;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.dto.ImageAvatarDto;
import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.member.dto.request.MemberDeleteRequest;
import com.createver.server.domain.member.dto.request.MemberJoinRequest;
import com.createver.server.domain.member.dto.request.MemberUpdateRequest;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.service.MemberService;
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
import java.util.Optional;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("Member Controller 테스트")
@WebMvcTest(controllers = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 테스트")
    @WithMockCustomMember
    void joinTest() throws Exception {

        //given
        MemberJoinRequest joinRequest = new MemberJoinRequest("user@example.com", "nickname", "password12", "password12");
        String content = objectMapper.writeValueAsString(joinRequest);

        MemberDto mockMember = new MemberDto(1L, "user@example.com", "nickname", "password12", null, LocalDateTime.now(), LocalDateTime.now(), null, false);
        when(memberService.join(anyString(), anyString(), anyString())).thenReturn(mockMember);

        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf().asHeader())
                                .content(content)
                );

        actions
                .andExpect(status().isCreated())
                .andDo(document(
                                "join-member",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("password").description("사용자 비밀번호"),
                                        fieldWithPath("checkPassword").description("사용자 비밀번호 확인"),
                                        fieldWithPath("nickname").description("사용자 닉네임")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result.memberId").description("멤버 ID"),
                                        fieldWithPath("result.email").description("멤버 이메일"),
                                        fieldWithPath("result.nickname").description("멤버 닉네임"),
                                        fieldWithPath("result.createdAt").description("멤버 가입날짜")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원정보 업데이트 테스트")
    @WithMockCustomMember
    void updateTest() throws Exception {
        // given
        MemberUpdateRequest request = new MemberUpdateRequest("updatedNickname", "updatedProfileImageUrl");
        String content = objectMapper.writeValueAsString(request);

        MemberDto mockMember = new MemberDto(
                1L,
                "test@example.com",
                request.getNickName(),
                "password",
                request.getProfileImage(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                false
        );
        when(memberService.update(anyString(), eq(Optional.of(request.getNickName())), eq(Optional.of(request.getProfileImage())))).thenReturn(mockMember);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/member/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "update-member",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("nickName").description("새로운 닉네임").optional(),
                                fieldWithPath("profileImage").description("새로운 프로필 이미지 URL").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.memberId").description("멤버 ID"),
                                fieldWithPath("result.email").description("멤버 이메일"),
                                fieldWithPath("result.nickName").description("멤버 닉네임"),
                                fieldWithPath("result.profileImage").description("멤버 프로필 이미지"),
                                fieldWithPath("result.planType").description("멤버 플랜 정보"),
                                fieldWithPath("result.oauthUser").description("로그인 타입")
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    @WithMockCustomMember
    void deleteTest() throws Exception {
        // given
        String testPassword = "testPassword";
        MemberDeleteRequest deleteRequest = new MemberDeleteRequest(testPassword);
        String requestContent = objectMapper.writeValueAsString(deleteRequest);

        doNothing().when(memberService).delete(anyString(), eq(testPassword));

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/member/exit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(requestContent)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document("delete-member",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result").description("응답 결과")
                                )
                        )
                );
    }


    @Test
    @DisplayName("회원정보 조회 테스트")
    @WithMockCustomMember
    void getMemberInfoTest() throws Exception {
        // given
        MemberDto mockMember = new MemberDto(1L, "user@example.com", "nickname", "password12", "profileImageUrl", LocalDateTime.now(), LocalDateTime.now(), null, false);
        when(memberService.getMemberInfo(anyString())).thenReturn(mockMember);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/member/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        //then
        actions
                .andExpect(status().isOk())
                .andDo(document("get-member-info",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result.memberId").description("멤버 ID"),
                                        fieldWithPath("result.email").description("멤버 이메일"),
                                        fieldWithPath("result.nickName").description("멤버 닉네임"),
                                        fieldWithPath("result.profileImage").description("멤버 프로필 이미지"),
                                        fieldWithPath("result.planType").description("멤버 플랜 정보"),
                                        fieldWithPath("result.oauthUser").description("로그인 타입")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원의 갤러리 목록 조회 테스트")
    @WithMockCustomMember
    void getMyGalleriesTest() throws Exception {
        // given
        List<GalleryDto> galleries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GalleryDto galleryDto = new GalleryDto(
                    i + 1L,
                    "Test Prompt " + i,
                    "http://storage.url" + i,
                    0,
                    LocalDateTime.now(),
                    5L,
                    10 + i,
                    2 + i,
                    1,
                    false
            );
            galleries.add(galleryDto);
        }
        Page<GalleryDto> page = new PageImpl<>(galleries, PageRequest.of(0, 20), 1);
        when(memberService.getMyGalleries(anyString(), any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/member/my-galleries")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "get-my-galleries",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc], [likeCount, desc])")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("페이징 처리된 회원의 갤러리 목록과 관련된 결과 데이터"),
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
    @DisplayName("회원의 아바타 목록 조회 테스트")
    @WithMockCustomMember
    void getMyAvatarTest() throws Exception {
        // given
        List<ImageAvatarDto> imageAvatars = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ImageAvatarDto imageAvatarDto = new ImageAvatarDto(
                    i + 1L,
                    "Test Prompt " + i,
                    1 + i,
                    "test style",
                    "Test Url",
                    10 + i,
                    2 + i,
                    "Test Prompt",
                    50,
                    "Test Url",
                    "Test Status",
                    "Test Id" + i,
                    new MemberDto(i + 1L, "test" + i + "@example.com", "김테스트", "password", "imageUrl", LocalDateTime.now(), LocalDateTime.now(), null, false), // member
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            imageAvatars.add(imageAvatarDto);
        }
        Page<ImageAvatarDto> page = new PageImpl<>(imageAvatars, PageRequest.of(0, 20), 1);
        when(memberService.getMyAvatar(anyString(), any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/member/my-avatar")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "get-my-avatars",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc], [likeCount, desc])")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("페이징 처리된 회원의 갤러리 목록과 관련된 결과 데이터"),
                                subsectionWithPath("result.content[]").description("아바타 목록"),
                                fieldWithPath("result.content[].avatarId").description("아바타 ID"),
                                fieldWithPath("result.content[].prompt").description("프롬프트"),
                                fieldWithPath("result.content[].numSteps").description("학습 단계 수"),
                                fieldWithPath("result.content[].styleName").description("스타일 이름"),
                                fieldWithPath("result.content[].inputImage").description("사용된 이미지"),
                                fieldWithPath("result.content[].numOutputs").description("출력 갯수"),
                                fieldWithPath("result.content[].guidanceScale").description("guidanceScale"),
                                fieldWithPath("result.content[].negativePrompt").description("부정 프롬프트"),
                                fieldWithPath("result.content[].styleStrengthRatio").description("styleStrengthRatio"),
                                fieldWithPath("result.content[].resultImageUrl").description("최종 이미지"),
                                fieldWithPath("result.content[].status").description("이미지 상태"),
                                fieldWithPath("result.content[].predictionId").description("모델 고유 번호"),
                                subsectionWithPath("result.content[].member").description("아바타 생성자 정보"),
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