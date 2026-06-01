package zeph_server.global.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class S3ImageService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final S3Client s3Client;
    private final String bucket;
    private final String publicBaseUrl;

    public S3ImageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.s3.public-base-url}") String publicBaseUrl
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl;
    }

    public String uploadProfileImage(Long userId, MultipartFile file) {
        return upload("profiles/" + userId, file);
    }

    public String uploadGroupImage(Long groupId, MultipartFile file) {
        return upload("groups/" + groupId, file);
    }

    public String toPublicUrl(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }
        if (imageKey.startsWith("http://") || imageKey.startsWith("https://")) {
            return imageKey;
        }
        return publicBaseUrl.replaceAll("/$", "") + "/" + imageKey.replaceAll("^/", "");
    }

    private String upload(String prefix, MultipartFile file) {
        validate(file);

        String imageKey = prefix + "/" + UUID.randomUUID() + extension(file);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 파일을 읽을 수 없습니다.");
        }

        return imageKey;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일이 필요합니다.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다.");
        }
    }

    private String extension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension == null || extension.isBlank()) {
            return switch (file.getContentType()) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                default -> "";
            };
        }
        return "." + extension.toLowerCase();
    }
}
