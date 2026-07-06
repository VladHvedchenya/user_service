package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillServiceImpl skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillMapper skillMapper;

    @Captor
    private ArgumentCaptor<Skill> captor;

    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> userSkillGuaranteeCaptor;

    private final int REQUIRED_OFFERS = 3;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(skillService, "requiredOffersCount", REQUIRED_OFFERS);
    }


    @Test
    public void testCreateSkillIfAlreadyExist() {
        //arrange
        CreateSkillDto createSkillDto = new CreateSkillDto("title");
        when(skillRepository.existsByTitle(createSkillDto.title())).thenReturn(true);
        //act + assert
        assertThrows(
                DataValidationException.class,
                () -> skillService.createSkill(createSkillDto));
        verify(skillRepository).existsByTitle(createSkillDto.title());
        verify(skillMapper, never()).toSkill(createSkillDto);
        verify(skillRepository, never()).save(any());
        verify(skillMapper, never()).toSkillDto(any());
    }

    @Test
    public void testCreateSKillIfNotExist() {
        //arrange
        CreateSkillDto createSkillDto = new CreateSkillDto("Java");
        when(skillRepository.existsByTitle(createSkillDto.title())).thenReturn(false);

        Skill createdSkill = new Skill();
        createdSkill.setTitle("Java");
        when(skillMapper.toSkill(createSkillDto)).thenReturn(createdSkill);

        Skill savedSkill = new Skill();
        savedSkill.setTitle("Java");
        savedSkill.setId(1L);
        when(skillRepository.save(createdSkill)).thenReturn(savedSkill);

        SkillDto response = new SkillDto(1L, "Java", null);
        when(skillMapper.toSkillDto(savedSkill)).thenReturn(response);

        //act
        SkillDto result = skillService.createSkill(createSkillDto);

        //assert
        assertEquals("Java", result.title());
        verify(skillRepository).existsByTitle("Java");
        verify(skillMapper).toSkill(createSkillDto);
        verify(skillRepository).save(captor.capture());
        Skill capturedSkill = captor.getValue();
        assertEquals("Java", capturedSkill.getTitle());
        assertNull(capturedSkill.getId());
        verify(skillMapper).toSkillDto(savedSkill);
    }

    @Test
    public void testGetKillsByUserId() {
        //arrange
        Long userId = 1L;
        Skill first = new Skill();
        first.setTitle("Java");
        Skill second = new Skill();
        second.setTitle("Python");
        List<Skill> skills = List.of(first, second);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        SkillDto firstSkillDto = new SkillDto(null, "Java", null);
        SkillDto secondSkillDto = new SkillDto(null, "Python", null);
        when(skillMapper.toSkillDto(first)).thenReturn(firstSkillDto);
        when(skillMapper.toSkillDto(second)).thenReturn(secondSkillDto);

        //act
        List<SkillDto> result = skillService.getByUserId(userId);

        //assert
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).title());
        assertEquals("Python", result.get(1).title());

        verify(skillRepository).findAllByUserId(userId);
        verify(skillMapper).toSkillDto(first);
        verify(skillMapper).toSkillDto(second);
    }

    @Test
    public void testGetOfferedSkills() {
        //arrange
        Long userId = 1L;
        Skill first = new Skill();
        first.setTitle("Java");
        first.setId(1L);
        Skill second = new Skill();
        second.setTitle("Python");
        second.setId(2L);
        List<Skill> skills = List.of(first, second);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);

        SkillDto firstSkillDto = new SkillDto(1L, "Java", null);
        SkillDto secondSkillDto = new SkillDto(2L, "Python", null);

        when(skillMapper.toSkillDto(first)).thenReturn(firstSkillDto);
        when(skillMapper.toSkillDto(second)).thenReturn(secondSkillDto);

        when(skillOfferRepository.countAllOffersOfSkill(first.getId(), userId)).thenReturn(3);
        when(skillOfferRepository.countAllOffersOfSkill(second.getId(), userId)).thenReturn(1);

        //act
        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        //assert
        assertEquals(2, result.size());
        SkillCandidateDto firstCandidate = result.get(0);
        assertEquals(firstSkillDto, firstCandidate.skill());
        assertEquals(3, firstCandidate.offersAmount());

        SkillCandidateDto secondCandidate = result.get(1);
        assertEquals(secondSkillDto, secondCandidate.skill());
        assertEquals(1, secondCandidate.offersAmount());

        verify(skillRepository).findSkillsOfferedToUser(userId);
        verify(skillMapper).toSkillDto(first);
        verify(skillMapper).toSkillDto(second);
        verify(skillOfferRepository).countAllOffersOfSkill(first.getId(), userId);
        verify(skillOfferRepository).countAllOffersOfSkill(second.getId(), userId);
    }

    @Test
    public void testGetOfferedSkillsWithEmptyList() {
        //arrange
        Long userId = 1L;
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of());

        //act
        List<SkillCandidateDto> skillCandidateDto = skillService.getOfferedSkills(userId);

        //assert
        assertEquals(0, skillCandidateDto.size());

        verify(skillRepository).findSkillsOfferedToUser(userId);
        verifyNoInteractions(skillMapper, skillOfferRepository);
    }

    @Test
    public void testAcquireSkillFromOfferWithNotEnoughCount() {
        //arrange
        Long skillId = 1L;
        Long userId = 1L;
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of());

        //act + assert
        assertThrows(
                DataValidationException.class,
                () -> skillService.acquireSkillFromOffer(skillId, userId)
        );

        verify(skillOfferRepository).findAllOffersOfSkill(skillId, userId);
        verifyNoInteractions(skillRepository, userSkillGuaranteeRepository);
    }

    @Test
    void testAcquireSkillFromOfferWithExistSkill() {
        //arrange
        Long skillId = 1L;
        Long userId = 1L;
        List<SkillOffer> mockOffers = List.of(new SkillOffer(), new SkillOffer(), new SkillOffer());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(mockOffers);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(new Skill()));

        //act + assert
        assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffer(skillId, userId));

        verify(skillOfferRepository).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository).findUserSkill(skillId, userId);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verifyNoInteractions(userSkillGuaranteeRepository);
    }

    @Test
    public void testAcquireSkillFromOfferWhenValid() {
        //arrange
        Long skillId = 1L;
        Long userId = 1L;
        Skill skill = new Skill();
        skill.setId(skillId);
        User receiver = new User();
        User author = new User();
        Recommendation recommendation = new Recommendation();
        recommendation.setReceiver(receiver);
        recommendation.setAuthor(author);

        SkillOffer firstOffer = new SkillOffer();
        firstOffer.setSkill(skill);
        firstOffer.setRecommendation(recommendation);

        SkillOffer secondOffer = new SkillOffer();
        secondOffer.setSkill(skill);
        secondOffer.setRecommendation(recommendation);

        SkillOffer thirdOffer = new SkillOffer();
        thirdOffer.setSkill(skill);
        thirdOffer.setRecommendation(recommendation);

        List<SkillOffer> offers = List.of(firstOffer, secondOffer, thirdOffer);

        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());

        //act
        skillService.acquireSkillFromOffer(skillId, userId);

        //assert
        verify(skillOfferRepository).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository).findUserSkill(skillId, userId);
        verify(skillRepository).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository).saveAll(userSkillGuaranteeCaptor.capture());

        List<UserSkillGuarantee> guarantees = userSkillGuaranteeCaptor.getValue();
        assertEquals(3, guarantees.size());

        for (var guarantee : guarantees) {
            assertEquals(skill, guarantee.getSkill());
            assertEquals(author, guarantee.getGuarantor());
            assertEquals(receiver, guarantee.getUser());
        }
    }
}