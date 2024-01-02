package com.createver.server.domain.member.entity;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.global.audit.AuditingFields;
import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "is_active = true")
@Table(name = "member",
       indexes = {@Index(name = "idx_email", columnList = "email")})
@Entity
public class Member extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long memberId;

    @Column(name = "email", nullable = false, unique = true, updatable = false, length = 50)
    private String email;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @Column(name = "is_oauth_user", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isOauthUser = false;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gallery> galleries = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Builder
    public Member(String email, String nickName, String password, String profileImage, List<String> roles, boolean isOauthUser) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.profileImage = profileImage;
        this.roles = roles;
        this.isOauthUser = isOauthUser;
    }

    public void updateMemberInfo(String nickName, String profileImage) {
        if (nickName != null && !nickName.isEmpty()) {
            this.nickName = nickName;
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            this.profileImage = profileImage;
        }
    }

    public void memberPlanUpdate(Plan plan){
        this.plan = plan;
    }

    public void deactivateMember() {
        this.isActive = false;
    }

    public void activateMember() {
        this.isActive = true;
    }

}
