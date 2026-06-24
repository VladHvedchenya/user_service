package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

/**
 * Сервис для управления профессиональными навыками (скиллами) пользователей.
 * Обеспечивает создание уникальных навыков, учет рекомендаций от других участников
 * и процесс социального подтверждения (приобретения) навыков.
 */
public interface SkillService {
    /**
     * Создает новый глобальный навык в системе.
     * @param skillDto данные для создания навыка (название).
     * @return полностью сформированный {@link SkillDto} с присвоенным ID.
     * @throws DataValidationException если навык с таким названием уже существует в БД.
     */
    SkillDto createSkill(CreateSkillDto skillDto);

    /**
     * Возвращает список всех подтвержденных навыков конкретного пользователя.
     *
     * @param userId идентификатор пользователя, чьи навыки нужно получить
     * @return список {@link SkillDto} с информацией о гарантах
     */
    List<SkillDto> getByUserId(Long userId);

    /**
     * Возвращает список навыков, которые были предложены пользователю другими участниками,
     * но еще не были им приобретены.
     *
     * @param userId идентификатор текущего пользователя
     * @return список {@link SkillCandidateDto} с количеством предложений по каждому навыку
     */
    List<SkillCandidateDto> getOfferedSkills(Long userId);

    /**
     * Позволяет пользователю присвоить себе предложенный навык, если он набрал
     * необходимое количество социальных подтверждений (офферов).
     *
     * @param skillId идентификатор приобретаемого навыка
     * @param userId  идентификатор пользователя, который приобретает навык
     * @throws DataValidationException если у пользователя уже есть этот навык,
     *                                 или если количество рекомендаций ниже установленного порога
     */
    void acquireSkillFromOffer(Long skillId, Long userId);
}