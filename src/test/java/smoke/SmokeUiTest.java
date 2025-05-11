package smoke;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.pages.CreateProjectPage;
import ui.pages.CreateTaskPage;
import ui.pages.LoginPage;
import ui.pages.RegisterPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;
import static org.assertj.core.api.Assertions.*;

public class SmokeUiTest {
    private static final String BASE_URL = "http://localhost:8081";
    private static String lastCreatedProjectHash;

    private void openPath(String path) {
        open(BASE_URL + path);
    }

    @BeforeAll
    public static void setUp() {
       Configuration.headless = true;
    }

    @Test
    public void registerTest() {
        openPath("/registration");
        RegisterPage registerPage = new RegisterPage();
        registerPage.register("smoke", "smoke@mail.com", "smoke", "smoke");

        boolean isCorrectUrl = webdriver().driver().getCurrentFrameUrl().equals(BASE_URL + "/login");
        boolean isErrorMessageShown = registerPage.getMismatchMessage().has(text("Пользователь с таким email уже существует"));

        assertThat(isCorrectUrl || isErrorMessageShown)
                .as("Должен быть либо правильный URL, либо сообщение об ошибке")
                .isTrue();
    }

    @Test
    public void loginTest() {
        openPath("/login");
        LoginPage loginPage = new LoginPage();
        loginPage.login("smoke@mail.com", "smoke");
        webdriver().shouldHave(url(BASE_URL + "/home"));

        String username = $$("div.text-center").first().$("h6 a").getText();
        assertThat(username).isEqualTo("smoke");
    }

    @Test
    public void createProjectTest() {
        openPath("/home/create");
        CreateProjectPage createProjectPage = new CreateProjectPage();
        createProjectPage.createProject("smoke project", "smoke project");

        webdriver().shouldHave(url(BASE_URL + "/home"));

        lastCreatedProjectHash = $("a[href^='/projects/']")
                .shouldBe(visible)
                .getAttribute("href")
                .replace(BASE_URL + "/projects/", "")
                .replace("/tasks", "");

        $(".card-title span").shouldHave(text("smoke project"));
        $(".card-text").shouldHave(text("smoke project"));
    }

    @Test
    public void navigateToProjectTasksAndCreateOneTest() {
        assertThat(lastCreatedProjectHash).isNotNull();
        openPath("/projects/" + lastCreatedProjectHash + "/tasks");
        webdriver().shouldHave(url(BASE_URL + "/projects/" + lastCreatedProjectHash + "/tasks"));

        CreateTaskPage tasksPage = new CreateTaskPage();
        CreateTaskPage.TaskModal modal = tasksPage.openAddTaskModal();

        String taskName = "smoke task";
        String taskDescription = "smoke task description";

        modal.setName(taskName)
                .setDescription(taskDescription)
                .setDeadline()
                .selectPriority("MEDIUM")
                .submit();

        tasksPage.shouldSeeTaskWithTitle(taskName);
        tasksPage.shouldSeeTaskWithDescription(taskDescription);
    }
}