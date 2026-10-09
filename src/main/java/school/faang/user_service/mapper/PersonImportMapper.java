package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.importer.Person;
import school.faang.user_service.dto.user.importer.PersonCsvDto;
import school.faang.user_service.dto.user.importer.PreviousEducation;
import school.faang.user_service.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonImportMapper {
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "contactInfo.email", source = "email")
    @Mapping(target = "contactInfo.phone", source = "phone")
    @Mapping(target = "contactInfo.address.street", source = "street")
    @Mapping(target = "contactInfo.address.city", source = "city")
    @Mapping(target = "contactInfo.address.state", source = "state")
    @Mapping(target = "contactInfo.address.country", source = "country")
    @Mapping(target = "contactInfo.address.postalCode", source = "postalCode")
    @Mapping(target = "education.faculty", source = "faculty")
    @Mapping(target = "education.yearOfStudy", source = "yearOfStudy")
    @Mapping(target = "education.major", source = "major")
    @Mapping(target = "education.gpa", source = "gpa")
    @Mapping(target = "previousEducation", expression = "java(toPreviousEducationList(csvDto))")
    Person toPerson(PersonCsvDto csvDto);

    @Mapping(target = "username", expression = "java(toUsername(person))")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "education", ignore = true)
    User toUser(Person person);

    default List<PreviousEducation> toPreviousEducationList(PersonCsvDto csvDto) {
        PreviousEducation previousEducation = PreviousEducation.builder()
                .degree(csvDto.getDegree())
                .institution(csvDto.getInstitution())
                .completionYear(csvDto.getCompletionYear())
                .build();

        List<PreviousEducation> result = new ArrayList<>();
        result.add(previousEducation);
        return result;
    }

    default String toUsername(Person person) {
        return (person.getFirstName() + person.getLastName()).toLowerCase();
    }
}
