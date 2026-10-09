package school.faang.user_service.dto.user.importer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentId;
    private ContactInfo contactInfo;
    private Education education;
    private String status;
    private LocalDate admissionDate;
    private LocalDate graduationDate;
    private List<PreviousEducation> previousEducation;
    private Boolean scholarship;
    private String employer;
}
