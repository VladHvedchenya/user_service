package school.faang.user_service.config.context;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            throw new DataValidationException(
                    "x-user-id header is required");
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
