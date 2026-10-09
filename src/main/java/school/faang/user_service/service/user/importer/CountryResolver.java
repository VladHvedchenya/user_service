package school.faang.user_service.service.user.importer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.CountryRepository;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CountryResolver {
    private final CountryRepository countryRepository;

    public Map<String, Country> loadCountriesByTitle() {
        Map<String, Country> countriesByTitle = new HashMap<>();
        countryRepository.findAll()
                .forEach(country -> countriesByTitle.put(normalize(country.getTitle()), country));
        return countriesByTitle;
    }

    public Country resolve(String countryTitle, Map<String, Country> countriesByTitle) {
        validateCountryTitle(countryTitle);

        String normalizedCountryTitle = normalize(countryTitle);
        Country country = countriesByTitle.get(normalizedCountryTitle);
        if (country != null) {
            return country;
        }

        Country savedCountry = countryRepository.save(Country.builder()
                .title(countryTitle.trim())
                .build());
        countriesByTitle.put(normalizedCountryTitle, savedCountry);
        return savedCountry;
    }

    private void validateCountryTitle(String countryTitle) {
        if (StringUtils.isBlank(countryTitle)) {
            throw new DataValidationException("Country should be present!");
        }
    }

    private String normalize(String countryTitle) {
        return countryTitle.trim().toLowerCase();
    }
}
