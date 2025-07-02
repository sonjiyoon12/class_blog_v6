package com.tenco.blog.reply;

import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

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
        User sessionUser = (User) session.getAttribute("sessionUser");
        replyService.save(saveDTO, sessionUser);
        return "redirect:/board/" + saveDTO.getBoardId();
    }
}
