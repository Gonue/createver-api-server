package com.template.server.domain.member.entity;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gallery> galleries = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    public static Member of(String email, String nickName, String encodedPwd){
        Member entity = new Member();
        entity.setEmail(email);
        entity.setNickName(nickName);
        entity.setPassword(encodedPwd);
        return entity;
    }
}
