package ui.pages;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CreateProjectPage {
    public void createProject(String title, String description) {
        $(By.id("title")).setValue(title);
        $(By.id("description")).setValue(description);

        $$("button.btn.btn-primary")
                .findBy(Condition.exactText("Сохранить"))
                .submit();
    }
}
