package com.main.Jora.integration;

import com.main.Jora.JoraApplication;
import com.main.Jora.models.User;
import com.main.Jora.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = JoraApplication.class)
public class UserRepositoryTest extends AbstractTestContainers {
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void beforeAll() {
        init();
    }

    @Test
    @DisplayName("Сохранение и поиск пользователя по email")
    void testSaveAndFindByEmail() {
        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setUsername("username");
        userRepository.save(user);

        User found = userRepository.findByEmail("email@email.com");

        assertThat(found).isNotNull();
        Assertions.assertEquals(user, found);
    }
}
