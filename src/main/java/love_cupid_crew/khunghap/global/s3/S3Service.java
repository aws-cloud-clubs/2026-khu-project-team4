package love_cupid_crew.khunghap.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.presigned-expiration-seconds}")
    private int presignedExpirationSeconds;

    private static final String TEMP_PREFIX     = "temp/";
    private static final String PROFILES_PREFIX = "profiles/";

    public record PresignedPutResult(String presignedUrl, String tempKey) {}

    public String generatePresignedGetUrl(String key) {
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(r -> r.bucket(bucket).key(key).build())
                .build();
        return s3Presigner.presignGetObject(request).url().toString();
    }

    public PresignedPutResult generatePresignedPutUrl() {
        String tempKey = TEMP_PREFIX + UUID.randomUUID() + ".jpg";

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedExpirationSeconds))
                .putObjectRequest(r -> r
                        .bucket(bucket)
                        .key(tempKey)
                        .contentType("image/jpeg")
                        .build())
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return new PresignedPutResult(presigned.url().toString(), tempKey);
    }

    public boolean doesObjectExist(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    // MultipartFile을 profiles/{userId}/{uuid}.jpg 로 직접 업로드하고 URL 반환
    public String uploadToProfiles(MultipartFile file, Long userId) throws IOException {
        String key = PROFILES_PREFIX + userId + "/" + UUID.randomUUID() + ".jpg";
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .build(),
            RequestBody.fromBytes(file.getBytes())
        );
        return buildPublicUrl(key);
    }

    // temp/{uuid}.jpg → profiles/{userId}/{uuid}.jpg 복사 후 temp 삭제
    public String copyToProfiles(String tempKey, Long userId) {
        String filename   = tempKey.substring(TEMP_PREFIX.length());
        String profileKey = PROFILES_PREFIX + userId + "/" + filename;

        s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(tempKey)
                .destinationBucket(bucket)
                .destinationKey(profileKey)
                .build());

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(tempKey)
                .build());

        return buildPublicUrl(profileKey);
    }

    private String buildPublicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}