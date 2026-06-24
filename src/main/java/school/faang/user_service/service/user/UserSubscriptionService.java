package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

/**
 * Сервис для управления социальными подписками между пользователями.
 * Обеспечивает создание и удаление связей типа "подписчик-автор",
 * а также агрегацию данных о количестве и списках подписок/подписчиков.
 */
public interface UserSubscriptionService {
    /**
     * Оформляет подписку одного пользователя на другого.
     *
     * @param followerId  идентификатор пользователя, который подписывается (подписчик)
     *
     * @param followeeId  идентификатор пользователя, на которого подписываются (автор)
     *
     * @throws DataValidationException если пользователь пытается подписаться сам на себя,
     *                                 или если подписка на этого автора уже существует в БД
     */
    void followUser(Long followerId, Long followeeId);

    /**
     * Отменяет существующую подписку пользователя на автора.
     *
     * @param followerId  идентификатор пользователя, который отменяет подписку
     *
     * @param followeeId  идентификатор автора, от которого отписываются
     *
     * @throws DataValidationException если пользователь пытается отписаться от самого себя,
     *                                 или если подписка на данного автора изначально отсутствовала
     */
    void unFollowUser(Long followerId, Long followeeId);

    /**
     * Возвращает общее количество подписчиков указанного пользователя.
     * Смотреть данную информацию может любой участник системы.
     *
     * @param followeeId  идентификатор пользователя, количество подписчиков которого нужно подсчитать
     *
     * @return {@link CountResponse}, содержащий итоговое число подписчиков
     */
    CountResponse getFollowersCount(Long followeeId);

    /**
     * Возвращает общее количество подписок (авторов), на которых подписан указанный пользователь.
     * Смотреть данную информацию может любой участник системы.
     *
     * @param followerId  идентификатор пользователя, количество подписок которого нужно подсчитать
     *
     * @return {@link CountResponse}, содержащий итоговое число подписок
     */
    CountResponse getFolloweesCount(Long followerId);

    /**
     * Возвращает профили всех подписчиков пользователя с возможностью динамической фильтрации.
     *
     * @param userId     идентификатор целевого пользователя
     *
     * @param filtersDto объект с критериями фильтрации (имя, телефон, опыт)
     *
     * @return отфильтрованный список {@link UserDto} подписчиков
     */
    List<UserDto> getFollowers(Long userId, UserFiltersDto filtersDto);

    /**
     * Возвращает профили всех авторов (подписок), на которых подписан пользователь,
     * с возможностью динамической фильтрации.
     *
     * @param userId     идентификатор целевого пользователя
     *
     * @param filtersDto объект с критериями фильтрации (имя, телефон, опыт)
     *
     * @return отфильтрованный список {@link UserDto} подписок
     */
    List<UserDto> getFollowees(Long userId, UserFiltersDto filtersDto);
}