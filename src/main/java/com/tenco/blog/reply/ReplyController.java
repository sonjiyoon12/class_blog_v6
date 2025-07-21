package com.tenco.blog.reply;

import com.tenco.blog.user.User;
import com.tenco.blog.utils.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ReplyController {

    private final ReplyService replyService;

    // 댓글 저장 기능 요청
    @PostMapping("/reply/save")
    public String save(ReplyRequest.SaveDTO saveDTO, HttpSession session) {
        // 인증 검사 (인터셉터에서 처리)
        // 유효성 검사
        saveDTO.validate();
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        replyService.save(saveDTO, sessionUser);
        return "redirect:/board/" + saveDTO.getBoardId();
    }

    // 댓글 삭제 기능 요청
    @PostMapping("/reply/{id}/delete")
    public String delete(@PathVariable(name = "id") Long replyId,
                         @RequestParam(name = "boardId") Long boardId,
                         HttpSession session){
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        replyService.deleteById(replyId, sessionUser);

        return "redirect:/board/" + boardId;
    }
}
