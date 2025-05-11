package com.main.Jora.util;

import com.main.Jora.api.RestAuthSession;
import io.restassured.http.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parser {
    public static String getFirstProjectHash(RestAuthSession auth) {
        String html = auth.authorizedRequest()
                .accept(ContentType.HTML)
                .get("/home")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href*=/projects/]");

        for (var link : links) {
            String href = link.attr("href");
            String hash = extractProjectHashFromHref(href);
            if (hash != null) {
                return hash;
            }
        }
        throw new RuntimeException("Не удалось найти ссылку с projectHash в HTML");
    }

    private static String extractProjectHashFromHref(String href) {
        int index = href.indexOf("/projects/");
        if (index == -1) return null;

        String after = href.substring(index + "/projects/".length());
        int slashIndex = after.indexOf('/');
        return slashIndex == -1 ? after : after.substring(0, slashIndex);
    }
}
