package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserSubscriptionServiceTest {
    @InjectMocks
    private UserSubscriptionServiceImpl userSubscriptionService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Test
    public void testFollowUserForHimself() {
        //follower - кто подписывается
        //followee - на кого подписываются
        //arrange
        Long followerId = 1L;
        Long followeeId = 1L;

        //act + assert
        assertThrows(
                DataValidationException.class,
                () -> userSubscriptionService.followUser(followerId, followeeId)
        );

        verifyNoInteractions(subscriptionRepository);
    }

    @Test
    public void testFollowUserWhenAlreadyFollowed() {
        //arrange
        Long followerId = 1L;
        Long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        //act + assert
        assertThrows(
                DataValidationException.class,
                () -> userSubscriptionService.followUser(followerId, followeeId)
        );

        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository, never()).followUser(followerId, followeeId);
    }

    @Test
    public void testFollowUserCorrectly() {
        //arrange
        Long followerId = 1L;
        Long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        //act
        userSubscriptionService.followUser(followerId, followeeId);

        //assert
        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserFromHimself() {
        //arrange
        Long followerId = 1L;
        Long followeeId = 1L;

        //act + assert
        assertThrows(
                DataValidationException.class,
                () -> userSubscriptionService.unFollowUser(followerId, followeeId)
        );

        verifyNoInteractions(subscriptionRepository);
    }

    @Test
    public void testUnfollowUserWhenNotSubscribed() {
        //arrange
        Long followerId = 1L;
        Long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        //act + assert
        assertThrows(
                DataValidationException.class,
                () -> userSubscriptionService.unFollowUser(followerId, followeeId)
        );

        verify(subscriptionRepository, never()).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserCorrectly() {
        //arrange
        Long followerId = 1L;
        Long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        //act
        userSubscriptionService.unFollowUser(followerId, followeeId);

        //assert
        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowersCount() {
        //arrange
        Long followeeId = 1L;
        Long expectedCount = 10L;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(expectedCount);

        //act
        CountResponse response = userSubscriptionService.getFollowersCount(followeeId);

        //assert
        assertEquals(expectedCount, response.count());
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    public void testGetFolloweesCount() {
        //arrange
        Long followerId = 1L;
        Long expectedCount = 10L;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);

        //act
        CountResponse response = userSubscriptionService.getFolloweesCount(followerId);

        //assert
        assertEquals(expectedCount, response.count());
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    public void testGetFolloweesWithNoFilters() {
        //arrange
        Long userId = 1L;
        User firstUser = new User();
        User secondUser = new User();
        when(subscriptionRepository.findByFollowerId(userId))
                .thenAnswer(inv -> Stream.of(firstUser, secondUser));
        when(userMapper.toUserDto(any())).thenAnswer(
                invocation -> new UserDto(1L,
                        "test",
                        "test@gmail.com",
                        "testPhone",
                        "test")
        );

        //act
        List<UserDto> users = userSubscriptionService.getFollowees(userId, null);

        //assert
        assertEquals(2, users.size());
        verify(subscriptionRepository).findByFollowerId(userId);
        verify(userMapper, times(2)).toUserDto(any());
    }

    @Test
    public void testGetFolloweesWithNameFilter() {
        //arrange
        Long userId = 1L;
        User first = new User();
        User second = new User();
        first.setUsername("Vlad");
        second.setUsername("Sergey");
        when(subscriptionRepository.findByFollowerId(userId))
                .thenAnswer(inv -> Stream.of(first, second));
        when(userMapper.toUserDto(any())).thenAnswer(
                inv -> new UserDto(
                        1L, "Vlad", "test", "test", "test"
                )
        );
        UserFiltersDto nameFilter = new UserFiltersDto("vla", null, null, null);

        //act
        List<UserDto> users = userSubscriptionService.getFollowees(userId, nameFilter);

        //assert
        assertEquals(1, users.size());
        verify(subscriptionRepository).findByFollowerId(userId);
        verify(userMapper, times(1)).toUserDto(any());
    }

    @Test
    public void testGetFolloweesWithPhoneFilter() {
        //arrange
        Long userId = 1L;
        User first = new User();
        User second = new User();
        first.setPhone("375297189359");
        second.setPhone("375298149795");
        when(subscriptionRepository.findByFollowerId(userId)).thenAnswer(
                inv -> Stream.of(first, second)
        );
        when(userMapper.toUserDto(any())).thenAnswer(
                inv -> new UserDto(
                        1L, "test", "test", "test", "test"
                )
        );
        UserFiltersDto phoneFilter = new UserFiltersDto(null, "375297", null, null);

        //act
        List<UserDto> users = userSubscriptionService.getFollowees(userId, phoneFilter);

        //assert
        assertEquals(1, users.size());
        verify(subscriptionRepository).findByFollowerId(userId);
        verify(userMapper, times(1)).toUserDto(any());
    }

    @Test
    public void testGetFolloweesWithMinExperienceFilter() {
        //arrange
        Long userId = 1L;
        User first = new User();
        User second = new User();
        first.setExperience(10);
        second.setExperience(20);
        when(subscriptionRepository.findByFollowerId(userId)).thenAnswer(
                inv -> Stream.of(first, second)
        );
        when(userMapper.toUserDto(any())).thenAnswer(
                inv -> new UserDto(
                        1L, "test", "test", "test", "test"
                )
        );
        UserFiltersDto minExperienceFilter = new UserFiltersDto(null, null, 15, null);

        //act
        List<UserDto> users = userSubscriptionService.getFollowees(userId, minExperienceFilter);

        //assert
        assertEquals(1, users.size());
        verify(subscriptionRepository).findByFollowerId(userId);
        verify(userMapper, times(1)).toUserDto(any());
    }

    @Test
    public void testGetFolloweesWithMaxExperienceFilter() {
        //arrange
        Long userId = 1L;
        User first = new User();
        User second = new User();
        first.setExperience(10);
        second.setExperience(20);
        when(subscriptionRepository.findByFollowerId(userId)).thenAnswer(
                inv -> Stream.of(first, second)
        );
        when(userMapper.toUserDto(any())).thenAnswer(
                inv -> new UserDto(
                        1L, "test", "test", "test", "test"
                )
        );
        UserFiltersDto maxExperienceFilter = new UserFiltersDto(null, null, null, 15);

        //act
        List<UserDto> users = userSubscriptionService.getFollowees(userId, maxExperienceFilter);

        //assert
        assertEquals(1, users.size());
        verify(subscriptionRepository).findByFollowerId(userId);
        verify(userMapper, times(1)).toUserDto(any());
    }

    @Test
    public void getFollowersWithNoFilter() {
        //arrange
        Long userId = 1L;
        User firstUser = new User();
        User secondUser = new User();
        when(subscriptionRepository.findByFolloweeId(userId))
                .thenAnswer(inv -> Stream.of(firstUser, secondUser));
        when(userMapper.toUserDto(any())).thenAnswer(
                invocation -> new UserDto(1L,
                        "test",
                        "test@gmail.com",
                        "testPhone",
                        "test")
        );

        //act
        List<UserDto> users = userSubscriptionService.getFollowers(userId, null);

        //assert
        assertEquals(2, users.size());
        verify(subscriptionRepository).findByFolloweeId(userId);
        verify(userMapper, times(2)).toUserDto(any());
    }
}