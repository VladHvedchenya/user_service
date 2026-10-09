package school.faang.user_service.dto.user.importer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreviousEducation {
    private String degree;
    private String institution;
    private Integer completionYear;
}
