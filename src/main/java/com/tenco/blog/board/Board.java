package com.tenco.blog.board;

import com.tenco.blog.reply.Reply;
import com.tenco.blog.user.User;
import com.tenco.blog.utils.MyDateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
// 기본 생성자 - JPA에서 엔티티는 기본 생성자가 필요
@Data
// @Table : 실제 데이터베이스 테이블 명을 지정할 때 사용
@Table(name = "board_tb")
// @Entity : JPA가 이 클래스를 데이터베이스 테이블과 매핑하는 객체(엔티티)로 인식
// 즉, @Entity 어노테이션이 있어야 JPA가 이 객체를 관리 한다.
@Entity
public class Board {

    // @Id 이 필드가 기본키(Primary key) 임을 나타냄
    @Id
    // IDENTITY 전략 : 데이터베이스의 기본 전략을 사용한다 -> Auto_Increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 별도 어노테이션이 없으면 필드명이 컬럼명이 됨
    private String title;
    private String content;
    // v2 에서 사용했던 방식
    // private String username;

    // v3 에서 Board 엔티티는 User 엔티티와 연관 관계가 성립이 된다

    // 다대일 (연관 관계)
    // 여러개의 게시글에는 한명의 작성자를 가질 수 있다
    // LAZY: 지연로딩으로 성능 최적화 (User 정보가 필요할 때만 조회)
    // FetchType.EAGER : 즉시 로딩 다 가져옴
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키 컬럼명 명시
    private User user; // Board 에 User 들어가 있음

    // CreationTimestamp : 하이버네이트가 제공하는 어노테이션
    // 엔티티가 처음 저장할 때 현재 시간을 자동을 설정한다.
    // pc -> db (날짜 주입)
    // v1 에서는 SQL now()를 직접 사용했지만 JPA 가 자동 처리
    @CreationTimestamp
    private Timestamp createdAt; //created_at (스네이크 케이스로 자동 변환)

    // 게시글에 소유자를 직접 확인하는 기능을 만들자
    public boolean isOwner(Long checkUserid) {
        return  this.user.getId().equals(checkUserid);
    }

    // 머스태치에서 표현할 시간을 포맷기능을(행위) 스스로 만들자
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }


    /**
     * 게시글과 댓글을 양방향 맵핑으로 설계 하겠다.
     * 하나의 게시글(one)에는 여러개의 댓글(many)을 가질 수 있다.
     *
     * 테이블 기준으로 고민을 해 본다면 게시글 테이블과 댓글 테이블 관계를
     * 형성할 때 fk는 누가 들고 있어야 맞는가?
     * Board Reply 테이블 간에 fk 는 Reply이 가지고 있어야 한다.
     * mappedBy: 외래키 주인이 아닌 엔티티에 설정해야 한다. 보드에 외래키가 없다는걸 알려줌
     *
     * cascade = CascadeType.REMOVE
     * 영속성 전이(전파)
     * - 게시글 삭제 시 관련된 모든 댓글고 자동 삭제 처리함
     * - 데이터 무결성 보장
     */
    @OrderBy("id DESC") // 정렬 옵션 설정
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.REMOVE)
    //mappedBy -> fk를 가지고 있으면 안된다는 의미
    List<Reply> replies = new ArrayList<>(); // 리스트로 댓글 불러오기 List 선언과 동시에 초기화
}
