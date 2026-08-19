package school.faang.user_service.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String code,
    String message,
    String path,
    Map<String, List<String>> violations
) {
}