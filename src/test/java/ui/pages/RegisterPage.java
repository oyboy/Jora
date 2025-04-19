package ui.pages;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class RegisterPage {
    public void register(String username, String email, String password, String confirmPassword) {
        $("#username").setValue(username);
        $("#email").setValue(email);
        $("#password").setValue(password);
        $("#confirmPassword").setValue(confirmPassword);
        $("button[type=submit]").click();
    }
    public SelenideElement getMismatchMessage() {
        return $(".alert.alert-danger");
    }
    public String getEmailErrorMessage() {
        SelenideElement emailInput = $("#email");
        boolean isValid = executeJavaScript("return arguments[0].checkValidity();", emailInput);
        Assertions.assertFalse(isValid);

        return executeJavaScript("return arguments[0].validationMessage;", emailInput);
    }
}