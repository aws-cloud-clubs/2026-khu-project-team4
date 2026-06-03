package love_cupid_crew.khunghap.user.controller;

import love_cupid_crew.khunghap.user.dto.UserProfileResponse;
import love_cupid_crew.khunghap.user.dto.UserProfileUpdateRequest;
import love_cupid_crew.khunghap.user.dto.UserProfileUpdateResponse;
import love_cupid_crew.khunghap.user.dto.UserActiveUpdateRequest;
import love_cupid_crew.khunghap.user.dto.UserActiveUpdateResponse;
import love_cupid_crew.khunghap.user.dto.UserPhotoUpdateResponse;
import love_cupid_crew.khunghap.user.service.UserService;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자의 전체 프로필 정보 조회
     * GET /api/users/me
     *
     * 현재는 Security 인증 객체 설정 전이므로, 임시로 userId = 1L로 설정
     * 나중에 @AuthenticationPrincipal을 사용하여 실제 인증된 사용자 ID로 변경
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse profileResponse = userService.getMyProfile(userDetails.getUserId());
        return ResponseEntity.ok(profileResponse);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileUpdateResponse> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateRequest req) {
        UserProfileUpdateResponse resp = userService.updateProfile(userDetails.getUserId(), req);
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/me/active")
    public ResponseEntity<UserActiveUpdateResponse> updateMyActiveStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserActiveUpdateRequest req) {
        UserActiveUpdateResponse resp = userService.updateActiveStatus(userDetails.getUserId(), req);
        return ResponseEntity.ok(resp);
    }

    @PutMapping(value = "/me/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserPhotoUpdateResponse> updateMyPhotos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(name = "photos", required = false) MultipartFile[] photos,
            @RequestPart(name = "deleteIds", required = false) String deleteIdsJson,
            @RequestPart(name = "displayOrders", required = false) String displayOrdersJson) throws Exception {
        Long currentUserId = userDetails.getUserId();

        // JSON 문자열 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        List<Long> deleteIds = (deleteIdsJson != null && !deleteIdsJson.isBlank())
                ? objectMapper.readValue(deleteIdsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class))
                : List.of();

        List<Short> displayOrders = (displayOrdersJson != null && !displayOrdersJson.isBlank())
                ? objectMapper.readValue(displayOrdersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Short.class))
                : List.of();

        UserPhotoUpdateResponse resp = userService.updatePhotos(currentUserId, photos, deleteIds, displayOrders);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUser(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
