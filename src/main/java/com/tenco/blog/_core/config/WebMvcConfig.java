package com.tenco.blog._core.config;

import com.tenco.blog._core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration // IoC 처리 (싱글톤 패턴 관리)
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // DI 처리 (생성자 의존 주입)
    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginInterceptor())
                // 인터셉터가 동작할 URI 패턴을 지정
                .addPathPatterns("/board/**","/user/**","/reply/**") // /board 와 /user가 들어간 경로 가로채기
                // 인터셉터에서 제외할 URL 패턴 설정
                .excludePathPatterns("/board/{id:\\d+}");
                // \\d+ 는 정규표현식으로 1개 이상의 숫자를 의미
                // /board/1, /board2
    }

    /**
     * addResourceHandlers
     * 외부 디렉토리의 파일을 웹에서 접근 가능하게 하는 설정
     * 1. Spring.. 기본적으로 static 폴더의 파일만 웹에서 접근 가능하게 한다.(기본값)
     * 2. 사용자가 업로드한 파일을 외부 디렉토리 (C:/uploads/profiles/ ) 저장할 예정
     * 3. 외부 디렉토리는 기본적으로 스프링이 인식을 하지 못함
     * 4. 따라서 수동으로 외부 디렉토리를 매핑 설정 해주어야 한다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:///" + uploadDir);
    }
}
