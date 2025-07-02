package com.tenco.blog.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 관련 데이터베이스 접근을 담당
 * 기본적인 CRUD 제공
 */
// @Repository 생략 가능 --> JpaRepository에 선언 되어 있음
public interface BoardJpaRepository extends JpaRepository<Board, Long> {

    // 기본 CRUD 기능 추가적인 기능은 직접 선언해주어야 한다.

    // 게시글과 사용자 정보가 포함된 엔티티를 만들어 주어야 한다.(게시글 리스트용)
    @Query("SELECT b FROM Board b JOIN FETCH b.user u ORDER BY b.id DESC")
    List<Board> findAllJoinUser();
    // JOIN FETCH 는 모든 Board 엔티티와 연관된 User를 한방 쿼리로 가져 옴 (이너조인을 하기 때문)
    // LAZY 전략이라서 N + 1 방지를 할 수 있다
    // : 게시글 10개가 있다면 지연로딩 1(Board 조회) + 10(User 조회) = 11번 쿼리가 발생

    // 게시글 ID로 한방에 유저 정보도 가져오기 - JOIN FETCH 사용하면 됨
    @Query("SELECT b FROM Board b JOIN FETCH b.user u WHERE b.id = :id")
    Optional<Board> findByIdJoinUser(@Param("id") Long id); // 인터페이스 추상메서드

}
