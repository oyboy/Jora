package com.main.Jora.api.rest;

import com.main.Jora.util.Parser;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import java.io.File;

import static org.hamcrest.Matchers.*;
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
}