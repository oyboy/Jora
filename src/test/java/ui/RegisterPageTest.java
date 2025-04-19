package ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.pages.RegisterPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;

public class RegisterPageTest {
    private static final String URL = "http://localhost:8081/registration";
    private RegisterPage registerPage = new RegisterPage();
    @Test
    public void testRegisterPageWithCorrectData() {
        open(URL);
        registerPage.register("test",
                "test@mail.com",
                "test",
                "test"
        );
        webdriver().shouldHave(url("http://localhost:8081/login"));
    }
    @Test
    public void testRegisterPageWithExistingUser() {
        open(URL);
        registerPage.register("testtttt",
                "test@mail.com",
                "test",
                "test"
        );
        registerPage.getMismatchMessage()
                .shouldHave(text("Пользователь с таким email уже существует"));
    }
    @Test
    public void testRegisterPageWithIncorrectPasswordConfirm() {
        open(URL);
        registerPage.register("test",
                "test@mail.com",
                "test",
                "testt"
        );
        $("form").shouldBe(visible);
        registerPage.getMismatchMessage()
                .shouldHave(text("Пароли не совпадают"));
    }
    @Test
    public void testRegisterPageWithIncorrectEmail() {
        open(URL);
        registerPage.register("test",
                "testmail.com",
                "test",
                "test"
        );
        String validationMessage = registerPage.getEmailErrorMessage();
        Assertions.assertTrue(!validationMessage.isEmpty());
    }
}