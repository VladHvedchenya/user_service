package school.faang.user_service.service.user.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordGeneratorTest {
    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    public void generatePasswordShouldReturnAlphanumericPassword() {
        String password = passwordGenerator.generatePassword();

        assertThat(password)
                .hasSize(16)
                .matches("[A-Za-z0-9]+");
    }
}
