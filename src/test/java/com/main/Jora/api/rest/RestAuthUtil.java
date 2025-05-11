package com.main.Jora.api.rest;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class RestAuthUtil {
    public static RestAuthSession loginAndGetSession(String username, String password) {
        RestAssured.baseURI = "http://localhost:8081";

        Response loginPage = given()
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String csrfToken = loginPage.htmlPath().getString("**.find { it.@name == '_csrf' }.@value");
        String jsessionId = loginPage.getCookie("JSESSIONID");

        SessionFilter sessionFilter = new SessionFilter();

        Response loginResponse = given()
                .filter(sessionFilter)
                .cookie("JSESSIONID", jsessionId)
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", username)
                .formParam("password", password)
                .formParam("_csrf", csrfToken)
                .when()
                .post("/login")
                .then()
                .statusCode(302)
                .extract().response();

        String newJsessionId = loginResponse.getCookie("JSESSIONID");

        Response csrfPage = given()
                .filter(sessionFilter)
                .cookie("JSESSIONID", newJsessionId)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .extract().response();
        String newCsrfToken = csrfPage.htmlPath().getString("**.find { it.@name == '_csrf' }.@value");

        return new RestAuthSession(sessionFilter, newCsrfToken, newJsessionId);
    }
}