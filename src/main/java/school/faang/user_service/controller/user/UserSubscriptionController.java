package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @PostMapping("/follow/{followeeId}")
    public void followUser(@PathVariable Long followeeId) {
        Long userId = userContext.getUserId();
        userSubscriptionService.followUser(userId, followeeId);
    }

    @PostMapping("/unfollow/{followeeId}")
    public void unFollowUser(@PathVariable Long followeeId) {
        Long userId = userContext.getUserId();
        userSubscriptionService.unFollowUser(userId, followeeId);
    }

    @GetMapping("/followers/count/{followeeId}")
    public CountResponse getFollowersCount(@PathVariable Long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followees/count/{followerId}")
    public CountResponse getFolloweesCount(@PathVariable Long followerId) {
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/followers/{userId}")
    public List<UserDto> getFollowers(@PathVariable Long userId, UserFiltersDto filtersDto) {
        return userSubscriptionService.getFollowers(userId, filtersDto);
    }

    @GetMapping("/followees/{userId}")
    public List<UserDto> getFollowees(@PathVariable Long userId, UserFiltersDto filtersDto) {
        return userSubscriptionService.getFollowees(userId, filtersDto);
    }
}