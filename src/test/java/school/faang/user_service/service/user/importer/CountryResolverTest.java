package school.faang.user_service.service.user.importer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.CountryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryResolverTest {
    @InjectMocks
    private CountryResolver countryResolver;

    @Mock
    private CountryRepository countryRepository;

    @Test
    public void loadCountriesByTitleShouldReturnCountriesMappedByNormalizedTitle() {
        Country usa = Country.builder()
                .title("USA")
                .build();
        when(countryRepository.findAll()).thenReturn(List.of(usa));

        Map<String, Country> result = countryResolver.loadCountriesByTitle();

        assertThat(result).containsEntry("usa", usa);
    }

    @Test
    public void resolveShouldReturnExistingCountryWhenCountryExists() {
        Country usa = Country.builder()
                .title("USA")
                .build();
        Map<String, Country> countriesByTitle = Map.of("usa", usa);

        Country result = countryResolver.resolve(" USA ", countriesByTitle);

        assertThat(result).isSameAs(usa);
        verify(countryRepository, never()).save(any());
    }

    @Test
    public void resolveShouldCreateCountryWhenCountryDoesNotExist() {
        Map<String, Country> countriesByTitle = new HashMap<>();
        Country savedCountry = Country.builder()
                .title("France")
                .build();
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        Country result = countryResolver.resolve(" France ", countriesByTitle);

        ArgumentCaptor<Country> countryCaptor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository).save(countryCaptor.capture());
        assertThat(countryCaptor.getValue().getTitle()).isEqualTo("France");
        assertThat(result).isSameAs(savedCountry);
        assertThat(countriesByTitle).containsEntry("france", savedCountry);
    }

    @Test
    public void resolveShouldThrowDataValidationExceptionWhenCountryTitleIsBlank() {
        assertThatThrownBy(() -> countryResolver.resolve(" ", new HashMap<>()))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("Country should be present!");

        verify(countryRepository, never()).save(any());
    }
}
