package com.main.Jora.api;

import io.restassured.http.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class DiscussionRestControllerTest {
    private static TestAuthSession auth;
    private static final String FILE_PATH = "src/test/resources/test-files/";
    private static String projectHash;

    @BeforeAll
    public static void setup() {
        auth = TestAuthUtil.loginAndGetSession("test@mail.com", "test");

        String html = given()
                .filter(auth.getSessionFilter())
                .cookie("JSESSIONID", auth.getJsessionId())
                .header("X-CSRF-TOKEN", auth.getCsrfToken())
                .accept(ContentType.HTML)
                .get("/home")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        Document doc = Jsoup.parse(html);
        Elements discussionLinks = doc.select("a[href^=/projects/][href$=/discussion]");
        if (!discussionLinks.isEmpty()) {
            String href = discussionLinks.first().attr("href");
            projectHash = href.split("/")[2];
        } else throw new RuntimeException("Не удалось найти projectHash в HTML");
    }


    @Test
    public void testUploadFilesSuccess() {
        File file1 = new File(FILE_PATH + "test1.txt");
        File file2 = new File(FILE_PATH + "test-image.jpg");

        given()
                .filter(auth.getSessionFilter())
                .cookie("JSESSIONID", auth.getJsessionId())
                .header("X-CSRF-TOKEN", auth.getCsrfToken())
                .multiPart("files", file1)
                .multiPart("files", file2)
                .when()
                .post("/api/v1/projects/" + projectHash + "/discussion/upload-files")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("[0]", not(emptyOrNullString()))
                .body("[1]", not(emptyOrNullString()));
    }
}