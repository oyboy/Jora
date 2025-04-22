package com.main.Jora.api;

import io.restassured.filter.session.SessionFilter;

public class TestAuthSession {
    private final SessionFilter sessionFilter;
    private final String csrfToken;
    private final String jsessionId;

    public TestAuthSession(SessionFilter sessionFilter, String csrfToken, String jsessionId) {
        this.sessionFilter = sessionFilter;
        this.csrfToken = csrfToken;
        this.jsessionId = jsessionId;
    }

    public SessionFilter getSessionFilter() {
        return sessionFilter;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public String getJsessionId() {
        return jsessionId;
    }
}