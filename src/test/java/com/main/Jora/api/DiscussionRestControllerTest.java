package com.main.Jora.api;

import com.main.Jora.util.Parser;
import io.restassured.http.ContentType;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class DiscussionRestControllerTest {
    private static RestAuthSession auth;
    private static final String FILE_PATH = "src/test/resources/test-files";
    private static String projectHash;

    @BeforeAll
    public static void setup() {
        auth = RestAuthUtil.loginAndGetSession("test@mail.com", "test");
        projectHash = Parser.getFirstProjectHash(auth);
    }

    @Test
    public void testUploadFilesSuccess() {
        File file1 = new File(FILE_PATH + "/test1.txt");
        File file2 = new File(FILE_PATH + "/test-image.jpg");

        auth.authorizedRequest()
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
    @Test
    public void testUploadFilesUnauthorized() {
        File file1 = new File(FILE_PATH + "/test1.txt");
        Response response = given()
                .multiPart("files", file1)
                .when()
                .post("/api/v1/projects/" + projectHash + "/discussion/upload-files")
                .then()
                .statusCode(302)
                .extract().response();

        String locationHeader = response.getHeader("Location");

        assertTrue(locationHeader != null && locationHeader.contains("login"), "Заголовок Location отсутствует или не содержит 'login'");
        assertThat(locationHeader, equalTo("http://localhost:8081/login"));
    }
}