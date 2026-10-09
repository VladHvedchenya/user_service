package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.importer.Person;
import school.faang.user_service.dto.user.importer.PersonCsvDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.PersonImportMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.user.generator.PasswordGenerator;
import school.faang.user_service.service.user.importer.CountryResolver;
import school.faang.user_service.service.user.parser.PersonCsvParser;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;

    private final CountryRepository countryRepository;

    private final UserMapper userMapper;
    private final PersonImportMapper personImportMapper;

    private final UserContext userContext;
    private final AvatarService avatarService;
    private final PersonCsvParser personCsvParser;
    private final PasswordGenerator passwordGenerator;
    private final CountryResolver countryResolver;

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);

        String s3AvatarKey = avatarService.generateAndSaveRandomAvatar(user.getEmail());

        if (s3AvatarKey != null) {
            UserProfilePic profilePic = new UserProfilePic();
            profilePic.setFileId(s3AvatarKey);
            profilePic.setSmallFileId(null);

            user.setUserProfilePic(profilePic);
        }

        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public void importUsers(InputStream inputStream) {
        log.info("Started users import from csv file");
        List<PersonCsvDto> csvDtos = personCsvParser.parse(inputStream);
        Map<String, Country> countriesByTitle = countryResolver.loadCountriesByTitle();

        for (PersonCsvDto csvDto : csvDtos) {
            User user = createImportedUser(csvDto, countriesByTitle);
            userRepository.save(user);
        }

        log.info("Finished users import from csv file. Imported users count: {}", csvDtos.size());
    }

    private User createImportedUser(PersonCsvDto csvDto, Map<String, Country> countriesByTitle) {
        Person person = personImportMapper.toPerson(csvDto);
        User user = personImportMapper.toUser(person);
        user.setPassword(passwordGenerator.generatePassword());
        user.setCountry(resolveCountry(person, countriesByTitle));
        return user;
    }

    private Country resolveCountry(Person person, Map<String, Country> countriesByTitle) {
        String countryTitle = person.getContactInfo()
                .getAddress()
                .getCountry();
        return countryResolver.resolve(countryTitle, countriesByTitle);
    }
}
