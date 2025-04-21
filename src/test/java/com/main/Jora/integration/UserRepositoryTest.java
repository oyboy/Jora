package com.main.Jora.integration;

import com.main.Jora.models.User;
import com.main.Jora.repositories.UserRepository;
import io.qameta.allure.Allure;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest extends AbstractTestContainers {
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void beforeAll() {
        init();
    }

    @Test
    @DisplayName("Сохранение и поиск пользователя по email")
    @Owner("oyboy")
    void testSaveAndFindByEmail() {
        Allure.step("Creating user");
        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setUsername("username");
        userRepository.save(user);

        Allure.step("Find user by email");
        User found = userRepository.findByEmail("email@email.com");

        Allure.step("Assert");
        assertThat(found).isNotNull();
        Assertions.assertEquals(user, found);
    }
    @Test
    @DisplayName("Поиск несуществующего пользователя по email")
    void FindByEmail() {
        User found = userRepository.findByEmail("email@email.com");
        assertThat(found).isNull();
    }
}
