package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;

import java.util.List;

public interface UserSubscriptionService {
    void followUser(Long followerId, Long followeeId);

    void unFollowUser(Long followerId, Long followeeId);

    CountResponse getFollowersCount(Long followeeId);

    CountResponse getFolloweesCount(Long followerId);

    List<UserDto> getFollowers(Long userId, UserFiltersDto filtersDto);

    List<UserDto> getFollowees(Long userId, UserFiltersDto filtersDto);
}