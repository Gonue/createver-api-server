package com.createver.server.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Member Entity 테스트")
class MemberTest {

    @DisplayName("Member 생성 테스트")
    @Test
    void testMemberCreation() {
        // Given
        String email = "test@example.com";
        String nickName = "TestNick";
        String password = "TestPassword";
        String profileImage = "TestImage.jpg";
        List<String> roles = Collections.singletonList("USER");

        // When
        Member member = Member.builder()
                .email(email)
                .nickName(nickName)
                .password(password)
                .profileImage(profileImage)
                .roles(roles)
                .build();

        // Then
        assertEquals(email, member.getEmail());
        assertEquals(nickName, member.getNickName());
        assertEquals(password, member.getPassword());
        assertEquals(profileImage, member.getProfileImage());
        assertEquals(roles, member.getRoles());
    }

    @DisplayName("Member 정보 업데이트 테스트")
    @Test
    void testUpdateMemberInfo() {
        // Given
        Member member = new Member("test@example.com", "OriginalNick", "password", "original.jpg", Collections.singletonList("USER"));
        String updatedNickName = "UpdatedNick";
        String updatedProfileImage = "updated.jpg";

        // When
        member.updateMemberInfo(updatedNickName, updatedProfileImage);

        // Then
        assertEquals(updatedNickName, member.getNickName());
        assertEquals(updatedProfileImage, member.getProfileImage());
    }

    @DisplayName("Member 정보 업데이트 테스트 - 유효한 경우")
    @Test
    void testUpdateMemberInfoWithValidData() {
        // Given
        Member member = new Member("test@example.com", "OriginalNick", "password", "original.jpg", Collections.singletonList("USER"));
        String updatedNickName = "UpdatedNick";
        String updatedProfileImage = "updated.jpg";

        // When
        member.updateMemberInfo(updatedNickName, updatedProfileImage);

        // Then
        assertEquals(updatedNickName, member.getNickName());
        assertEquals(updatedProfileImage, member.getProfileImage());
    }

    @DisplayName("Member 정보 업데이트 테스트 - 빈 값 또는 null")
    @Test
    void testUpdateMemberInfoWithEmptyOrNull() {
        // Given
        Member member = new Member("test@example.com", "OriginalNick", "password", "original.jpg", Collections.singletonList("USER"));
        String originalNickName = member.getNickName();
        String originalProfileImage = member.getProfileImage();

        // When
        member.updateMemberInfo("", null);

        // Then
        assertEquals(originalNickName, member.getNickName()); // 닉네임 변경 없어야 함
        assertEquals(originalProfileImage, member.getProfileImage()); // 프로필 이미지 변경 없어야 함
    }


    @DisplayName("Member 플랜 업데이트 테스트")
    @Test
    void testMemberPlanUpdate() {
        // Given
        Member member = new Member("test@example.com", "NickName", "password", "image.jpg", Collections.singletonList("USER"));
        Plan mockPlan = mock(Plan.class);

        // When
        member.memberPlanUpdate(mockPlan);

        // Then
        assertEquals(mockPlan, member.getPlan());
    }
}