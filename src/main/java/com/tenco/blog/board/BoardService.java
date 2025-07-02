package com.tenco.blog.board;

import com.tenco.blog._core.errors.exception.Exception403;
import com.tenco.blog._core.errors.exception.Exception404;
import com.tenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Board 관련 비즈니스 로직을 처리하는 Service 계층
 */
@RequiredArgsConstructor
@Service // IoC 대상
@Transactional(readOnly = true)
// 모든 메서드를 읽기 전용 트랜잭션으로 실행 (findAll, findById 최적화)
// 성능 최적화 (변경 감지 비활성화), 데이터 수정 방지()
// 데이터베이스 락(lock) 최소화 하여 동시성 성능 개선
public class BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardService.class);
    private final BoardJpaRepository boardJpaRepository;

    /**
     * 게시글 저장
     */
    // 메서드 레벨에서의 트랜잭션 선언
    @Transactional // 데이터 수정이 필요하므로 읽기 전용 설정을 해제하고 쓰기 전용으로 변환
    public Board save(BoardRequest.SaveDTO saveDTO, User sessionUser) {
        // 1. 로그 기록 - 게시글 저장 요청 정보 출력
        // 2. DTO를 Entity로 변환(작성자 정보 포함)
        // 3. 데이터베이스에 게시글 저장
        // 4. 저장 완료 로그 기록
        // 5. 저장된 Board를 Controller로 반환
        log.info("게시글 저장 서비스 처리 시작 - 제목 {}, 작성자 {}",
                saveDTO.getTitle(), sessionUser.getUsername());
        Board board = saveDTO.toEntity(sessionUser);
        boardJpaRepository.save(board); // 영속화
        log.info("게시글 저장 완료 - ID {}, 제목 {}",
                board.getId(), board.getTitle());
        return board;
    }

    /**
     * 게시글 목록 조회
     */
    public List<Board> findAll() {
        // 1. 로그 기록
        // 2. 데이터베이스 게시글 조회
        // 3. 로그 기록
        // 4. 조회된 게시글 목록 반환
        log.info("게시글 조회 서비스 처리 시작");
        List<Board> boardList = boardJpaRepository.findAllJoinUser();
        log.info("게시글 목록 조회 완료 - 총 {} 개", boardList.size());
        return boardList;
    }

    /**
     * 게시글 상세 조회
     */
    public Board findById(Long id) {
        // 1. 로그 기록
        // 2. 데이터베이스에서 해당 board id로 조회 - where
        // 3. 게시글이 없다면 직접 404 에러처리
        // 4. 조회 성공 시 로그 기록
        // 5. 조회된 게시글 반환
        log.info("게시글 상세 조회 서비스 시작 - ID {}", id);
        Board board = boardJpaRepository.findByIdJoinUser(id).orElseThrow(() -> {
            log.warn("게시글 조회 실패 - ID {}", id);
            return new Exception404("게시글을 찾을 수 없습니다");
        });
        log.info("게시글 상세 조회 완료 - 제목 {}", board.getTitle());
        return board;
    }

    /**
     * 게시글 수정(권한 체크 포함)
     */
    @Transactional
    public Board updateById(Long id, BoardRequest.UpdateDTO updateDTO,
                            User sessionUser) {
        // 1. 로그 기록
        // 2. 수정하려는 게시글 조회
        // 3. 권한 체크
        // 4. 권한이 없다면 403 예외 발생
        // 5. Board 엔티티에 상태값 변경 (더티 체킹)
        // 6. 로그 기록 - 수정 완료
        // 7. 수정된 게시글 반환
        log.info("게시글 수정 서비스 시작 - 게시글 ID {}", id);
        Board board = boardJpaRepository.findById(id).orElseThrow(() -> {
            log.warn("게시글 조회 실패 - ID {}", id);
            return new Exception404("해당 게시글이 존재하지 않습니다");
        });

        // 3.
        if(!board.isOwner(sessionUser.getId())) {
            throw new Exception403("본인이 작성한 게시글만 수정 가능");
        }

        board.setTitle(updateDTO.getTitle()); // 필드값 상태 변경
        board.setContent(updateDTO.getContent()); // 필드값 상태 변경
        // TODO - board 엔티티에 update() 만들어 주기
        // 더티 체킹
        log.info("게시글 수정 완료 - 게시글 ID {}, 게시글 제목 {}", id, board.getTitle());
        return board;
    }

    /**
     * 게시글 삭제(권한 체크)
     */
    @Transactional
    public void deleteById(Long id, User sessionUser){
        // 1. 로그 기록
        // 2. 삭제 하려는 게시글 조회
        // 3. 권한 체크
        // 4. 권한이 없으면 403 예외 처리
        // 5. 데이터베이스 삭제 처리
        // 6. 삭제 완료 로그 기록
        log.info("게시글 삭제 서비스 시작 - ID {}", id);
        Board board = boardJpaRepository.findById(id).orElseThrow(() -> {
            return new Exception404("삭제하려는 게시글이 없습니다");
        });
        if(!board.isOwner(sessionUser.getId())){
            throw new Exception403("본인이 작성한 게시글만 삭제할 수 있습니다");
        }
        boardJpaRepository.deleteById(id);
    }

    /**
     * 게시글 소유자 확인 (수정 화면 요청)
     */
    public void checkBoardOwner(Long boardId, Long userId) {
        Board board = findById(boardId);
        if(!board.isOwner(userId)) {
            throw new Exception403("본인 게시글만 수정 할 수 있습니다.");
        }
    }
}




