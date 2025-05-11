package ui.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    public void login(String email, String password) {
        $("#email").setValue(email);
        $("#password").setValue(password);
        $("button[type=submit]").click();
    }
    public SelenideElement getMismatchMessage() {
        return $(".alert.alert-danger");
    }
}