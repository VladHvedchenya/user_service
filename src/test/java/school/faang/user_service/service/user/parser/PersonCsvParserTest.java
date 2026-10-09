package school.faang.user_service.service.user.parser;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.importer.PersonCsvDto;
import school.faang.user_service.exception.DataValidationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PersonCsvParserTest {
    private final PersonCsvParser parser = new PersonCsvParser();

    @Test
    public void parseShouldReturnPersonCsvDtos() {
        InputStream inputStream = csvInputStream(
                csvHeader(),
                "John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,"
                        + "New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,"
                        + "Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,"
                        + "2016,true,XYZ Technologies"
        );

        List<PersonCsvDto> result = parser.parse(inputStream);

        assertThat(result).hasSize(1);
        PersonCsvDto person = result.get(0);
        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getYearOfBirth()).isEqualTo(1998);
        assertThat(person.getStudentId()).isEqualTo("123456");
        assertThat(person.getEmail()).isEqualTo("johndoe@example.com");
        assertThat(person.getCountry()).isEqualTo("USA");
        assertThat(person.getGpa()).isEqualByComparingTo(BigDecimal.valueOf(3.8));
        assertThat(person.getAdmissionDate()).isEqualTo(LocalDate.of(2016, 9, 1));
        assertThat(person.getGraduationDate()).isEqualTo(LocalDate.of(2020, 5, 30));
        assertThat(person.getScholarship()).isTrue();
    }

    @Test
    public void parseShouldThrowDataValidationExceptionWhenCsvHasInvalidType() {
        InputStream inputStream = csvInputStream(
                csvHeader(),
                "John,Doe,invalid-year,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,"
                        + "New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,"
                        + "Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,"
                        + "2016,true,XYZ Technologies"
        );

        assertThatThrownBy(() -> parser.parse(inputStream))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("Failed to parse Csv file");
    }

    private InputStream csvInputStream(String header, String row) {
        return new ByteArrayInputStream((header + System.lineSeparator() + row)
                .getBytes(StandardCharsets.UTF_8));
    }

    private String csvHeader() {
        return String.join(",",
                "firstName",
                "lastName",
                "yearOfBirth",
                "group",
                "studentID",
                "email",
                "phone",
                "street",
                "city",
                "state",
                "country",
                "postalCode",
                "faculty",
                "yearOfStudy",
                "major",
                "GPA",
                "status",
                "admissionDate",
                "graduationDate",
                "degree",
                "institution",
                "completionYear",
                "scholarship",
                "employer"
        );
    }
}
