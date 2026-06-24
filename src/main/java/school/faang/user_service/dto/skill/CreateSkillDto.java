package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;

public record CreateSkillDto(
        @NotBlank
        String title
) {}