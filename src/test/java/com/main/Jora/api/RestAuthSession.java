package com.main.Jora.api;

import io.restassured.filter.session.SessionFilter;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestAuthSession {
    private final SessionFilter sessionFilter;
    private final String csrfToken;
    private final String jsessionId;

    public RestAuthSession(SessionFilter sessionFilter, String csrfToken, String jsessionId) {
        this.sessionFilter = sessionFilter;
        this.csrfToken = csrfToken;
        this.jsessionId = jsessionId;
    }

    public RequestSpecification authorizedRequest() {
        return given()
                .filter(sessionFilter)
                .cookie("JSESSIONID", jsessionId)
                .header("X-CSRF-TOKEN", csrfToken);
    }
}