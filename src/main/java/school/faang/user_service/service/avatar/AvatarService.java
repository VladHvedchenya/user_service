package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.dicebear.DiceBearClient;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final DiceBearClient dicebearClient;
    private final AmazonS3 amazonS3;

    @Value("${s3.bucket:user-avatars}")
    private String bucketName;

    @Value("${dicebear.api.style:pixel-art}")
    private String avatarStyle;

    public String generateAndSaveRandomAvatar(String email) {
        log.info("Starting random avatar generation for user UUID: {}", email);

        try (Response response = dicebearClient.getRandomAvatar(avatarStyle, email)) {
            if (response.status() != 200) {
                log.warn("DiceBear API returned error status: {}. Falling back to default avatar.", response.status());
                return null;
            }


            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");

            if (response.headers().containsKey("Content-Length")) {
                long contentLength = Long.parseLong(response.headers().get("Content-Length").iterator().next());
                metadata.setContentLength(contentLength);
            }

            if (!amazonS3.doesBucketExistV2(bucketName)) {
                amazonS3.createBucket(bucketName);
            }

            String uniqueFileName = java.util.UUID.randomUUID().toString();
            String s3Key = String.format("users/avatars/%s.png", uniqueFileName);
            InputStream inputStream = response.body().asInputStream();

            amazonS3.putObject(bucketName, s3Key, inputStream, metadata);
            log.info("Successfully uploaded random avatar to S3 with key '{}'", s3Key);

            return s3Key;

        } catch (Exception e) {
            log.error("Avatar integration service failed for user UUID: {}. User will get a default avatar.", email, e);
            return null;
        }
    }
}