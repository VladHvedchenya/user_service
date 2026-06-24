package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new DataValidationException("Вы не можете подписаться сами на себя!");
        }
        log.info("{} пытается подписаться на {}", followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Вы уже подписаны на этого пользователя!");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public void unFollowUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new DataValidationException("Вы не можете отписаться сами от себя!");
        }
        log.info("{} пытается отписаться от {}", followerId, followeeId);
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Вы не можете отписаться, потому что и так не подписаны!");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    @Override
    public CountResponse getFollowersCount(Long followeeId) {
        long followersCount = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new CountResponse(followersCount);
    }

    @Transactional(readOnly = true)
    @Override
    public CountResponse getFolloweesCount(Long followerId) {
        long followeesCount = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        return new CountResponse(followeesCount);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getFollowers(Long userId, UserFiltersDto filtersDto) {
        try (Stream<User> userStream = subscriptionRepository.findByFolloweeId(userId)) {
            return filterUsers(userStream, filtersDto)
                    .map(userMapper::toUserDto)
                    .toList();
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getFollowees(Long userId, UserFiltersDto filtersDto) {
        try (Stream<User> userStream = subscriptionRepository.findByFollowerId(userId)) {
            return filterUsers(userStream, filtersDto)
                    .map(userMapper::toUserDto)
                    .toList();
        }
    }

    private Stream<User> filterUsers(Stream<User> userStream, UserFiltersDto filters) {
        if (filters == null) {
            return userStream;
        }
        return userStream
                .filter(user -> filters.namePattern() == null
                        || filters.namePattern().isBlank()
                        ||
                        (user.getUsername() != null && user.getUsername()
                                .toLowerCase().contains(filters.namePattern().toLowerCase())))
                .filter(user -> filters.phonePattern() == null
                        || filters.phonePattern().isBlank()
                        || (user.getPhone() != null && user.getPhone().contains(filters.phonePattern())))
                .filter(user -> filters.experienceMin() == null
                        || (user.getExperience() >= filters.experienceMin()))
                .filter(user -> filters.experienceMax() == null
                        || (user.getExperience() <= filters.experienceMax()));
    }
}