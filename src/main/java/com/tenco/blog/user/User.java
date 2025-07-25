package com.tenco.blog.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Table(name = "user_tb")
@Entity
public class User {

    @Id //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 이름 중복 방지를 위한 유니크 제약 조건 설정
    @Column(unique = true)
    private String username;

    private String password;
    private String email;

    // 프로필 이미지 경로 필드 추가
    // Null 허용으로 선택적 기능 구현
    private String profileImagePath;

    // now() -> x
    // 엔티티가 영속화 될 때 자동으로 pc 현재시간을 설정해 준다
    @CreationTimestamp
    private Timestamp createdAt;

    // 객체 생성 시 가독성과 안정성 향상
    @Builder
    public User(Long id, String username, String password, String email, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
    }
}
