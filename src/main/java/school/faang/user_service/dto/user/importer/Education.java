package school.faang.user_service.dto.user.importer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    private String faculty;
    private Integer yearOfStudy;
    private String major;
    private BigDecimal gpa;
}
