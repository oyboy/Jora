package com.main.Jora.api;

import com.main.Jora.models.Project;
import com.main.Jora.util.Parser;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class HomeControllerTest {
    private static RestAuthSession auth;
    @BeforeAll
    public static void setup() {
        auth = RestAuthUtil.loginAndGetSession("test@mail.com", "test");
    }

    @Test
    public void createProjectAndVerifyItExists() {
        String title = "Test Project";
        String description = "Test Description";

        auth.authorizedRequest()
                .when()
                .formParam("title", title)
                .formParam("description", description)
                .post("/home/create")
                .then()
                .statusCode(302);

        String html = auth.authorizedRequest()
                .accept(ContentType.HTML)
                .get("/home")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        Document doc = Jsoup.parse(html);
        Elements projectCards = doc.select("div.card-body");

        assertFalse(projectCards.isEmpty(), "Список проектов не должен быть пустым");
        boolean exists = projectCards.stream().anyMatch(card -> {
            String foundTitle = card.selectFirst("h2.card-title span").text().trim();
            String foundDescription = card.selectFirst("p.card-text").text().trim();
            return foundTitle.equals("testTitle") && foundDescription.equals("testDescription");
        });
        assertTrue(exists, "Проект с указанным заголовком и описанием не найден");
    }
}