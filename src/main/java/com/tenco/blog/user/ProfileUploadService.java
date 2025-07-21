package com.tenco.blog.user;

import com.tenco.blog._core.errors.exception.Exception400;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * User 관련 비즈니스 로직
 * - 사용자의 프로필 이미지를 파일로 직접 생성하는 코드를 작성해 보자.
 * - 역할 : 실제 파일을 시스템에 저장하고 삭제하는 작업만 처리할 예정
 * - 주의 : 데이터베이스 업데이트는 UserService 에서 처리할 예정
 */
@Service
public class ProfileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 프로필 이미지 파일을 서버에 업로드 하는 메서드
     */
    public String uploadProfileImage(MultipartFile multipartFile) throws IOException {

        // 1단계 : 업로드할 디렉토리(폴더)가 존재하지 않으면 생성
        createUploadDirectory();

        // 2단계 : 업로드된 파일의 원본 이름 추출
        // DB <-- 실제 저장되는 경로, 사용자가 올린 파일명도 관리할 수 있다.
        // 예 abc.png
        String originalFileName = multipartFile.getOriginalFilename();

        // 3단계 : 파일 확장자를 추출(.jpg, .png 등)
        String extension = getFileExtension(originalFileName);

        // 4단계 : 중복을 방지하기 위해 고유한 파일명 생성
        // 예 : 20250721_143022_abc.png
        String uniqueFileName = generateUniqueFileName(extension);

        // 5단계 : 최종 저장할 파일의 전체 경로를 생성(아직 파일 저장 안한 상태)
        // 예: C:/uploads/profiles/2020123_123123.jpg
        Path filePath = Paths.get(uploadDir, uniqueFileName);

        // 6단계 : 실제로 파일을 임시 저장소에서 최종 우리가 정한 위치로 이동/복사
        multipartFile.transferTo(filePath);

        // 7단계 : 실제 바이트 단위로 받은 데이터를 서버 컴퓨터에
        // new File() 하고 경로와 실제 생성된 파일 명을 문자열로 총 반환
        return "/uploads/profiles/" + uniqueFileName;
    }

    // 고유한 파일명 생성
    private String generateUniqueFileName(String extension) {
        // 1. 현재 날짜와 시간을 "YYYYMMDD_HHmmss" 형태로 포맷
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // 2. UUID(범용 고유 식별자)를 생성하고 앞의 8자리만 사용
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        // 3. 최종 결과 : 202050721143022_123abc.jpg
        return timestamp + " _ "+ uuid + extension;

    }

    // 파일 확장자만 추출해주는 메서드
    private String getFileExtension(String originalFileName) {
        if(originalFileName == null || originalFileName.lastIndexOf(".") == -1) {
            return ""; // 확장자가 없으면 빈 문자열 반환 /.이 없으면 -1 반환
        }
        // 마지막 점(.) 문자 이후의 문자열을 확장자로 반환
        // profile.jpg --> lastIndexOf(".") --> 7 반환
        return originalFileName.substring(originalFileName.lastIndexOf("."));
        // profile.jpg <-- .jpg <-- 이 부분 추출 됨
    }

    // 폴더를 생성하는 메서드
    private void createUploadDirectory() throws IOException {
        // window, linux
        Path uploadPath = Paths.get(uploadDir);

        // 디렉토리가 존재하지 않으면 생성
        // C:/uploads/profiles/ 경로가 없으면
        if(Files.exists(uploadPath) == false) {
            // 여러 레벨의 디렉토리를 한번에 생성해 준다.
            Files.createDirectories(uploadPath);
        }
    }

    /**
     * 프로필 이미지를 삭제하는 메서드
     */
    public void deleteProfileImage(String imagePath){
        if(imagePath != null && imagePath.isEmpty() == false) {
            try{
                // uploads/profiles/1212132.jpg
                // 1단계 : 전체 경로에서 파일명만 추출
                String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);

                // 2단계 : 실제 파일 시스템 경로 생성
                Path filePath = Paths.get(uploadDir, fileName);

                // 3단계 : 파일이 존재하면 삭제, 없으면 아무것도 안함
                Files.deleteIfExists(filePath);

            } catch (IOException e) {
                throw new Exception400("프로필 이미지를 삭제하지 못하였습니다");
            }
        }
    }
}
