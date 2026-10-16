package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperNotificationTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @ParameterizedTest
    @EnumSource(PreferredContact.class)
    void shouldExposePreferredContactInUserDto(PreferredContact preference) {
        User user = User.builder()
                .id(1L)
                .username("Author")
                .email("author@example.com")
                .contactPreference(ContactPreference.builder().preference(preference).build())
                .build();

        UserDto result = mapper.toUserDto(user);

        assertThat(result).isEqualTo(new UserDto(1L, "Author", "author@example.com", null, null, preference));
    }

    @Test
    void shouldAllowUserWithoutContactPreference() {
        UserDto result = mapper.toUserDto(User.builder().id(1L).build());

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.preference()).isNull();
    }

    @Test
    void shouldMapNullUserToNull() {
        assertThat(mapper.toUserDto(null)).isNull();
    }
}
