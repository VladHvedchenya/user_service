package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.importer.Address;
import school.faang.user_service.dto.user.importer.ContactInfo;
import school.faang.user_service.dto.user.importer.Education;
import school.faang.user_service.dto.user.importer.Person;
import school.faang.user_service.dto.user.importer.PersonCsvDto;
import school.faang.user_service.dto.user.importer.PreviousEducation;
import school.faang.user_service.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class PersonImportMapperTest {
    private final PersonImportMapper mapper = Mappers.getMapper(PersonImportMapper.class);

    @Test
    public void toPersonShouldMapFlatCsvDtoToNestedPerson() {
        PersonCsvDto csvDto = createPersonCsvDto();

        Person result = mapper.toPerson(csvDto);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getStudentId()).isEqualTo("123456");
        assertThat(result.getAdmissionDate()).isEqualTo(LocalDate.of(2016, 9, 1));
        assertThat(result.getContactInfo().getEmail()).isEqualTo("johndoe@example.com");
        assertThat(result.getContactInfo().getPhone()).isEqualTo("+1-123-456-7890");
        assertThat(result.getContactInfo().getAddress().getCountry()).isEqualTo("USA");
        assertThat(result.getContactInfo().getAddress().getCity()).isEqualTo("New York");
        assertThat(result.getEducation().getFaculty()).isEqualTo("Computer Science");
        assertThat(result.getEducation().getGpa()).isEqualByComparingTo(BigDecimal.valueOf(3.8));
        assertThat(result.getPreviousEducation()).hasSize(1);
        assertThat(result.getPreviousEducation().get(0).getDegree()).isEqualTo("High School Diploma");
    }

    @Test
    public void toPersonShouldReturnMutablePreviousEducationList() {
        Person result = mapper.toPerson(createPersonCsvDto());

        assertThatCode(() -> result.getPreviousEducation().add(PreviousEducation.builder().build()))
                .doesNotThrowAnyException();
        assertThat(result.getPreviousEducation()).hasSize(2);
    }

    @Test
    public void toUserShouldMapPersonToUserAndIgnoreServiceManagedFields() {
        Person person = createPerson();

        User result = mapper.toUser(person);

        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.getEmail()).isEqualTo("johndoe@example.com");
        assertThat(result.getPhone()).isEqualTo("+1-123-456-7890");
        assertThat(result.getCity()).isEqualTo("New York");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getId()).isNull();
        assertThat(result.getPassword()).isNull();
        assertThat(result.getCountry()).isNull();
        assertThat(result.getEducation()).isNull();
    }

    private PersonCsvDto createPersonCsvDto() {
        PersonCsvDto csvDto = new PersonCsvDto();
        csvDto.setFirstName("John");
        csvDto.setLastName("Doe");
        csvDto.setYearOfBirth(1998);
        csvDto.setGroup("A");
        csvDto.setStudentId("123456");
        csvDto.setEmail("johndoe@example.com");
        csvDto.setPhone("+1-123-456-7890");
        csvDto.setStreet("123 Main Street");
        csvDto.setCity("New York");
        csvDto.setState("NY");
        csvDto.setCountry("USA");
        csvDto.setPostalCode("10001");
        csvDto.setFaculty("Computer Science");
        csvDto.setYearOfStudy(3);
        csvDto.setMajor("Software Engineering");
        csvDto.setGpa(BigDecimal.valueOf(3.8));
        csvDto.setStatus("Active");
        csvDto.setAdmissionDate(LocalDate.of(2016, 9, 1));
        csvDto.setGraduationDate(LocalDate.of(2020, 5, 30));
        csvDto.setDegree("High School Diploma");
        csvDto.setInstitution("XYZ High School");
        csvDto.setCompletionYear(2016);
        csvDto.setScholarship(true);
        csvDto.setEmployer("XYZ Technologies");
        return csvDto;
    }

    private Person createPerson() {
        return Person.builder()
                .firstName("John")
                .lastName("Doe")
                .contactInfo(ContactInfo.builder()
                        .email("johndoe@example.com")
                        .phone("+1-123-456-7890")
                        .address(Address.builder()
                                .city("New York")
                                .country("USA")
                                .build())
                        .build())
                .education(Education.builder()
                        .faculty("Computer Science")
                        .gpa(BigDecimal.valueOf(3.8))
                        .build())
                .build();
    }
}
