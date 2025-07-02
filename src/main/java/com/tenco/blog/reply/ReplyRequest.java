package com.tenco.blog.reply;

import com.tenco.blog._core.errors.exception.Exception400;
import com.tenco.blog.board.Board;
import com.tenco.blog.user.User;
import lombok.Data;

public class ReplyRequest {

    @Data
    public static class SaveDTO {
        private Long boardId; // 댓글이 달릴 게시글 id
        private String comment; // 댓글 내용

        /**
         * 입력 데이터 유효성 검증
         */
        public void validate() {
            if(comment == null || comment.trim().isEmpty()){
                throw new Exception400("댓글 내용을 입력하시오");
            }

            if(comment.length() > 500){
                throw new Exception400("댓글은 500자 이내로 작성해주세요");
            }

            if(boardId == null) {
                throw new Exception400("게시글 정보가 필요합니다");
            }
        }

        /**
         * 보통 Save DTO에 toEntity 메서드를 만들게 된다
         * 멤버 변수에 없는 데이터가 필요할 때는
         * 외부에서 주입 받으면 된다.
         */
        public Reply toEntity(User sessionUser, Board board) {
            return Reply.builder()
                    .comment(comment.trim()) // 앞뒤 공백 제거
                    .user(sessionUser)
                    .board(board)
                    .build();
        }
    }
}
