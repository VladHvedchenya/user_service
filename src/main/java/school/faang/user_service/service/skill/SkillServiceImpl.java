package school.faang.user_service.service.skill;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService{
    @Value("${skill.required-offers:3}")
    private int requiredOffersCount;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    @Override
    public SkillDto createSkill(CreateSkillDto skillDto) {
        log.info("Старт создания навыка с названием: {}", skillDto.title());
        if (skillRepository.existsByTitle(skillDto.title()))
            throw new DataValidationException("This skill already exist!");

        Skill skill = skillMapper.toSkill(skillDto);
        skill = skillRepository.save(skill);
        log.info("Успешно создан новый навык: [ID: {}, Title: {}]", skill.getId(), skill.getTitle());
        return skillMapper.toSkillDto(skill);
    }

    @Transactional
    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skillMapper::toSkillDto).toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills.stream()
                .map(skill -> new SkillCandidateDto(
                skillMapper.toSkillDto(skill),
                skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId)
        )).toList();
    }

    @Transactional
    @Override
    public void acquireSkillFromOffer(Long skillId, Long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < requiredOffersCount)
            throw new DataValidationException("Недостаточно рекомендаций для приобретения навыка!");
        if (skillRepository.findUserSkill(skillId, userId).isPresent())
            throw new DataValidationException("Такой навык уже есть у вас!");
        skillRepository.assignSkillToUser(skillId, userId);
        List<UserSkillGuarantee> guarantees = offers.stream()
                .map(
                        offer -> {
                            UserSkillGuarantee guarantee = new UserSkillGuarantee();
                            guarantee.setSkill(offer.getSkill());
                            guarantee.setUser(offer.getRecommendation().getReceiver());
                            guarantee.setGuarantor(offer.getRecommendation().getAuthor());
                            return guarantee;
                        }
                ).toList();
        userSkillGuaranteeRepository.saveAll(guarantees);
    }
}