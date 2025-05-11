package ui.pages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CreateTaskPage {
    public TaskModal openAddTaskModal() {
        $("button[data-bs-target='#addTaskModal']").shouldBe(visible).click();
        $("#addTaskModal").shouldBe(visible);
        return new TaskModal();
    }

    public void shouldSeeTaskWithTitle(String title) {
        $$(".card-title a").findBy(text(title)).shouldBe(visible);
    }

    public void shouldSeeTaskWithDescription(String description) {
        $$("p.card-text").findBy(text(description)).shouldBe(visible);
    }

    public static class TaskModal {
        public TaskModal setName(String name) {
            $("#addTaskModal #name").setValue(name);
            return this;
        }
        public TaskModal setDescription(String description) {
            $("#addTaskModal #description").setValue(description);
            return this;
        }

        public TaskModal selectPriority(String priority) {
            $("#addTaskModal input[name='priority'][value='" + priority + "']").parent().click();
            return this;
        }
        public TaskModal setDeadline() {
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
            String formattedDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            try {
                executeJavaScript(
                        "document.querySelector('input[name=\"deadline\"]').value = '" + formattedDate + "';"
                );
            } catch (Exception e) {
                $("input[name='deadline']").setValue(formattedDate);
            }
            return this;
        }

        public void submit() {
            $("#addTaskModal button[type='submit']").click();
            $("#addTaskModal").shouldBe(hidden);
        }
    }
}
