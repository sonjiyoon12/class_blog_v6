package com.tenco.blog.user;

import com.tenco.blog._core.errors.exception.Exception400;
import com.tenco.blog._core.errors.exception.Exception404;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true) // 클래스 레벨에서의 읽기 전용 설정
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserJpaRepository userJpaRepository;
    private final ProfileUploadService profileUploadService;

    /**
     * 프로필 이미지 업로드 서비스(DB 저장까지)
     * @param userId
     * @param multipartFile
     * @return
     */
    @Transactional
    public User uploadProfileImage(Long userId, MultipartFile multipartFile){
        User user = findById(userId);
        // 최초등록, 수정 시도 있음
        // 기존 이미지 경로
        String oldImagePath = user.getProfileImagePath();

        try{
            // 1. 새 이미지를 서버 컴퓨터에 생성 완료...
            String newImagePath = profileUploadService.uploadProfileImage(multipartFile);
            // 2. 기존 이미지가 있다면 서버 컴퓨터에서 파일 제거(공간 부족하니까)
            if(oldImagePath != null) {
                profileUploadService.deleteProfileImage(oldImagePath);
            }
            // 3. DB에 저장, 더티체킹 활용
            user.setProfileImagePath(newImagePath);
            return user;
        } catch (IOException e) {
            throw new Exception400("프로필 이미지 업로드에 실패했습니다");
        }
    }

    // 이미지 삭제만 처리하는 메서드
    @Transactional
    public User deleteProfileImage(Long userId) {
        User user = findById(userId);
        // BD에 저장된 이미지 경로 추출
        String imagePath = user.getProfileImagePath();

        user.setProfileImagePath(null); // 엔티티 상태값 변경 -> 자동 수정

        if(imagePath != null && imagePath.isEmpty() == false) {
            // 실제 서버에 존재하는 파일을 삭제 처리
            profileUploadService.deleteProfileImage(imagePath);
        }
        // 변경된 엔티티를 리턴(이미지 경로 null 처리된 상태)
        return user;
    }

    /**
     * 회원가입 처리
     */
    @Transactional // 메서드 레벨에서 쓰기 전용 트랜잭션 활성화
    public User join(UserRequest.JoinDTO joinDTO) {
        // 1. 사용자명 중복 체크
        userJpaRepository.findByUsername(joinDTO.getUsername())
                .ifPresent(user1 -> {
                    throw new Exception400("이미 존재하는 사용자명입니다");
                });
//        User user = joinDTO.toEntity();
//        User savedUser = userJpaRepository.save(user);
//        return user;

        return userJpaRepository.save(joinDTO.toEntity());
    }

    /**
     * 로그인 처리
     */
    public User login(UserRequest.LoginDTO loginDTO) {

       return userJpaRepository
                .findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
                .orElseThrow(() -> {
                    return new Exception400("사용자명 또는 비밀번호가 틀렸어요");
                });
    }

    /**
     * 사용자 정보 조회
     */
    public User findById(Long id) {
        return userJpaRepository.findById(id).orElseThrow(() -> {
           log.warn("사용자 조회 실패 - ID {}", id);
           return new Exception404("사용자를 찾을 수 없습니다");
        });
    }

    /**
     * 회원정보 수정 처리 (더티 체킹)
     */
    @Transactional
    public User updateById(Long userId, UserRequest.UpdateDTO updateDTO){
        // 1. 로그 기록
        // 2. 사용자 조회
        // 3. 수정된 User 반환 왜? --> 세션 동기화 때문!!
        User user = findById(userId);
        // user.update(updateDTO); TODO 추후 추가
        user.setPassword(updateDTO.getPassword());
        return user;
    }
}





