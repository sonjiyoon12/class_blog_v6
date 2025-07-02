package com.tenco.blog.reply;

import com.tenco.blog._core.errors.exception.Exception404;
import com.tenco.blog.board.Board;
import com.tenco.blog.board.BoardJpaRepository;
import com.tenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // final 키워드를 가진 멤버를 초기화 해
@Service // IoC 대상
public class ReplyService {

    private static final Logger log = LoggerFactory.getLogger(ReplyService.class);
    // DI 처리
    private final ReplyJpaRepository replyJpaRepository;
    private final BoardJpaRepository boardJpaRepository;

    // 댓글 저장 기능
    // 서비스 계층, Repository 계층에 메서드 이름 (같이 하거나, 다르게 정의)
    @Transactional
    public void save(ReplyRequest.SaveDTO saveDTO, User sessionUser) {
        log.info("댓글 저장 서비스 처리 시작 - 게시글 ID {}, 작성자 {} ",
                saveDTO.getBoardId(), sessionUser.getUsername());

        // 2. 댓글이 달릴 게시글 존재 여부 확인
        Board board = boardJpaRepository.findById(saveDTO.getBoardId())
                .orElseThrow(() -> new Exception404("존재하지 않는 게시글에는 댓글 작성 불가")); // return 없이도 가능

        // 3. 준 영속상태
        Reply reply = saveDTO.toEntity(sessionUser, board);

        // 4. 저장 - 정방향 insert 처리
        replyJpaRepository.save(reply);

    }
    // 댓글 삭제 기능
}
