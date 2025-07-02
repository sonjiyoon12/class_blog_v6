package com.tenco.blog.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor // DI 처리
@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * 회원 정보 수정 화면 요청
     */
    @GetMapping("/user/update-form")
    public String updateForm(Model model, HttpSession session) {

        User seeionUser = (User) session.getAttribute("sessionUser");
        User user = userService.findById(seeionUser.getId());
        model.addAttribute("user", user);
        return "user/update-form";
    }

    /**
     * 회원 수정 기능 요청
     */
    @PostMapping("/user/update")
    public String update(UserRequest.UpdateDTO reqDTO,
                         HttpSession session) {

        // 1. 인증 검사
        // 2. 유효성 검사
        // 3. 서비스 계층 -> 회원정보 수정 기능 위임
        // 4. 세션 동기화 처리
        // 5. 리다이렉트 -> 회원 정보 화면 요청(새로운 request)
        reqDTO.validate();
        User user = (User) session.getAttribute("sessionUser");
        User updateUser = userService.updateById(user.getId(), reqDTO);
        session.setAttribute("sessionUser", updateUser);
        return "redirect:/user/update-form";
    }

    /**
     * 회원 가입 화면 요청
     *
     * @return join-form.mustache
     */
    @GetMapping("/join-form")
    public String joinForm() {
        log.info("회원 가입 요청 폼");
        return "user/join-form";
    }

    /**
     * 회원 가입 기능 요청
     */
    @PostMapping("/join")
    public String join(UserRequest.JoinDTO joinDTO) {
        joinDTO.validate();
        userService.join(joinDTO);
        return "redirect:/login-form";
    }

    /**
     * 로그인 화면 요청
     */
    @GetMapping("/login-form")
    public String loginForm() {
        return "user/login-form";
    }

    /**
     * 로그인 요청
     */
    @PostMapping("/login")
    public String login(UserRequest.LoginDTO loginDTO, HttpSession session) {
        loginDTO.validate();
        User user = userService.login(loginDTO);
        session.setAttribute("sessionUser", user);
        return "redirect:/";
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 무효화
        session.invalidate();
        return "redirect:/";
    }
}
