package school.faang.user_service.config.context;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserContextTest {

    private final UserContext userContext = new UserContext();

    @Test
    public void testGetUserIdShouldReturnStoredUserId() {
        long expectedUserId = 42L;

        userContext.setUserId(expectedUserId);

        assertEquals(expectedUserId, userContext.getUserId());
    }

    @Test
    public void testGetUserIdShouldThrowExceptionWhenUserIdIsMissing() {
        DataValidationException exception = assertThrows(
                DataValidationException.class,
                userContext::getUserId
        );

        assertEquals("x-user-id header is required", exception.getMessage());
    }

    @Test
    public void testClearShouldRemoveStoredUserId() {
        userContext.setUserId(42L);

        userContext.clear();

        assertThrows(DataValidationException.class, userContext::getUserId);
    }
}
