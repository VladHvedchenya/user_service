package school.faang.user_service.dto.user.importer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonCsvDto {
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;

    @JsonProperty("studentID")
    private String studentId;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String faculty;
    private Integer yearOfStudy;
    private String major;

    @JsonProperty("GPA")
    private BigDecimal gpa;
    private String status;
    private LocalDate admissionDate;
    private LocalDate graduationDate;
    private String degree;
    private String institution;
    private Integer completionYear;
    private Boolean scholarship;
    private String employer;
}
