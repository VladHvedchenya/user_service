package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.importer.Address;
import school.faang.user_service.dto.user.importer.ContactInfo;
import school.faang.user_service.dto.user.importer.Person;
import school.faang.user_service.dto.user.importer.PersonCsvDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.PersonImportMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.user.generator.PasswordGenerator;
import school.faang.user_service.service.user.importer.CountryResolver;
import school.faang.user_service.service.user.parser.PersonCsvParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplImportTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PersonImportMapper personImportMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private AvatarService avatarService;

    @Mock
    private PersonCsvParser personCsvParser;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private CountryResolver countryResolver;

    @Test
    public void importUsersShouldParseMapEnrichAndSaveUsers() {
        InputStream inputStream = new ByteArrayInputStream("csv".getBytes(StandardCharsets.UTF_8));
        PersonCsvDto csvDto = new PersonCsvDto();
        Person person = createPerson();
        User user = new User();
        Country country = Country.builder()
                .title("USA")
                .build();
        Map<String, Country> countriesByTitle = new HashMap<>();

        when(personCsvParser.parse(inputStream)).thenReturn(List.of(csvDto));
        when(countryResolver.loadCountriesByTitle()).thenReturn(countriesByTitle);
        when(personImportMapper.toPerson(csvDto)).thenReturn(person);
        when(personImportMapper.toUser(person)).thenReturn(user);
        when(passwordGenerator.generatePassword()).thenReturn("generatedPassword");
        when(countryResolver.resolve("USA", countriesByTitle)).thenReturn(country);

        userService.importUsers(inputStream);

        assertThat(user.getPassword()).isEqualTo("generatedPassword");
        assertThat(user.getCountry()).isSameAs(country);
        verify(userRepository).save(user);
    }

    private Person createPerson() {
        return Person.builder()
                .contactInfo(ContactInfo.builder()
                        .address(Address.builder()
                                .country("USA")
                                .build())
                        .build())
                .build();
    }
}
