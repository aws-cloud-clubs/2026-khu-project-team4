package love_cupid_crew.khunghap.user.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DummyPhotoService {

    /**
     * 임시 파일 저장 서비스
     * 실제 AWS S3 연동은 나중에 구현
     * 현재는 더미 URL을 반환하기만 함
     */
    public List<String> uploadPhotos(MultipartFile[] files) {
        List<String> urls = new ArrayList<>();

        if (files == null) {
            return urls;
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String dummyUrl = generateDummyUrl(file.getOriginalFilename());
                urls.add(dummyUrl);
            }
        }

        return urls;
    }

    /**
     * 단일 파일 업로드
     */
    public String uploadPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return generateDummyUrl(file.getOriginalFilename());
    }

    /**
     * 더미 URL 생성
     * 실제 구현에서는 S3에 업로드하고 실제 URL을 반환
     */
    private String generateDummyUrl(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        return "https://dummy-url.com/" + uuid + ".jpg";
    }
}

