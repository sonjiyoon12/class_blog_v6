package com.tenco.blog.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

// @Import(UserJpaRepository.class) 인터페이스이기 때문에 @Import로 직접 빈으로 등록할 수 없다.
@DataJpaTest // JPA 관련 컴포넌트만 로드하여 테스트(가벼운 테스트)
public class UserJpaRepositoryTest {

    @Autowired // DI 처리
    private UserJpaRepository userJpaRepository;

    @Test
    public void save_test() {
        User testUser = User.builder()
                        .username("testUser")
                        .password("1234")
                        .email("a@naver.com")
                        .build();

        User savedUser = userJpaRepository.save(testUser);
        Assertions.assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void findByUsername_test() {
        String username = "ssar";

        Optional <User> selectedUser = userJpaRepository.findByUsername(username);

        System.out.println(selectedUser);
        System.out.println(selectedUser.get().getUsername());
    }
}
