package com.tenco.blog._core.errors.exception;

// 401 UnAuthorized  상황에서 사용할 커스텀 예외 클래스
public class Exception401 extends RuntimeException {

    // 에러 메세지로 사용할 문자열을 super 클래스에게 전달
    public Exception401(String message) {
        super(message);
    }

    // 로그인이 필요한 상황, 세션이 만료 되었을 때
}
