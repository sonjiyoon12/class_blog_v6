package com.tenco.blog.user;

import com.tenco.blog.utils.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor // DI 처리
@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final ProfileUploadService profileUploadService;

    /**
     * 회원 정보 수정 화면 요청
     */
    @GetMapping("/user/update-form")
    public String updateForm(Model model, HttpSession session) {

        // 머스태치 파일에서 sessionUser 키값 출력하는 코드들 있음
        User seeionUser = (User) session.getAttribute(Define.SESSION_USER);
        User user = userService.findById(seeionUser.getId());
        // 모델에서 관리하는 (가방) user 키값으로 머스태치에서 뿌려주고 있다.
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
        User user = (User) session.getAttribute(Define.SESSION_USER);
        User updateUser = userService.updateById(user.getId(), reqDTO);
        session.setAttribute(Define.SESSION_USER, updateUser);
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
        session.setAttribute(Define.SESSION_USER, user);
        return "redirect:/";
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 무효화
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/user/upload-profile-image")
    public String uploadProfileImage(@RequestParam(name = "profileImage")MultipartFile multipartFile,
                                     HttpSession session) {
        // 인증 검사는 인터셉터에서 처리
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);

        // 유효성 검사 (파일 유효성 검사)
        UserRequest.ProfileImageDTO profileImageDTO = new UserRequest.ProfileImageDTO();
        profileImageDTO.setProfileImage(multipartFile);
        profileImageDTO.validate();

        // 서비스에 일 위임(DB 저장 및 실제 파일 생성 까지)
        User updateUser = userService.uploadProfileImage(sessionUser.getId(), multipartFile);

        // 세션 값에 새로운 값을 재 갱신 해주어야 한다. (세션 재 갱신 처리)
        session.setAttribute(Define.SESSION_USER, updateUser);

        // 업로드 로직 구현 시작 ...
        return "redirect:/user/update-form";
    }

    @PostMapping("/user/delete-profile-image")
    public String deleteProfileImage(HttpSession session) {

        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        // DB 경로를 null 처리하고 실제 파일도 삭제 처리 함
        User updateUser = userService.deleteProfileImage(sessionUser.getId());

        // 세션 정보 업데이트 처리
        session.setAttribute(Define.SESSION_USER, updateUser);
        return "redirect:/user/update-form";
    }
}
