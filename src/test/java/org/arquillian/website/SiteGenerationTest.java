package org.arquillian.website;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import io.quarkiverse.roq.testing.RoqAndRoll;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.arquillian.website.data.model.IdentityData;
import org.arquillian.website.data.model.SiteData;
import org.arquillian.website.roq.GeneratedDataExtension;
import org.arquillian.website.roq.GeneratedSelection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks the minimum source contract for the first generated Roq page. */
@QuarkusTest
@RoqAndRoll(port = 8082)
class SiteGenerationTest {

    @Inject
    SiteData siteData;

    @Inject
    GeneratedSelection generatedSelection;

    @Inject
    IdentityData identities;

    @TempDir
    Path stagingDirectory;

    /** Ensures the homepage source and Qute layout are present before batch generation. */
    @Test
    void homepageUsesAsciiDocAndPageLayout() throws Exception {
        assertTrue(Files.exists(Path.of("content/index.adoc")));
        assertTrue(Files.exists(Path.of("templates/layouts/page.html")));
        assertTrue(Files.exists(Path.of("templates/layouts/base.html")));
        String layout = Files.readString(Path.of("templates/layouts/base.html"));
        assertTrue(layout.contains("<title>{#if page.title??}"));
    }

    /** Confirms Roq binds the curated data file before Qute rendering begins. */
    @Test
    void bindsTypedSiteData() {
        assertEquals("Arquillian", siteData.name());
        assertTrue(siteData.description().contains("testing"));
    }

    /** Confirms generated routes retain stable pretty output filenames for static export. */
    @Test
    void selectsGeneratedOutputPaths() {
        assertTrue(generatedSelection.pages().paths().stream()
                .anyMatch(path -> path.outputPath().equals("modules/arquillian-core/index.html")));
        assertTrue(generatedSelection.pages().paths().stream()
                .anyMatch(path -> path.outputPath().equals("community/aslakknutsen/index.html")));
    }

    /** Confirms the static identity snapshot includes the historical project mappings. */
    @Test
    void loadsHistoricalIdentitySnapshot() throws Exception {
        assertTrue(Files.exists(Path.of("data/identities.json")));
        assertTrue(Files.exists(Path.of("content/community/contributors.adoc")));
        assertEquals(358, identities.identities().size());
        Comparator<String> byLastName = Comparator.comparing((String name) -> {
            String trimmed = name.trim();
            return trimmed.substring(trimmed.lastIndexOf(' ') + 1);
        }).thenComparing((String name) -> name);
        assertEquals(identities.identities().stream().map(identity -> identity.name()).sorted(byLastName).toList(),
                identities.identities().stream().map(identity -> identity.name()).toList());
        assertTrue(identities.identities().stream()
                .anyMatch(identity -> identity.githubId().equals("spriadka")));
    }

    /** Confirms normalized provider output is staged outside tracked data directories. */
    @Test
    void stagesGeneratedDataSafely() throws Exception {
        GeneratedDataExtension loader = new GeneratedDataExtension(stagingDirectory,
                Path.of("data/curated/generated-pages.json"));

        assertTrue(loader.stage(loader.load()).startsWith(stagingDirectory));
        assertThrows(IllegalArgumentException.class,
                () -> new GeneratedDataExtension(Path.of("data"), Path.of("data/curated/generated-pages.json"))
                        .stage(loader.load()));
    }

    /** Confirms generated page families resolve through the running Roq router. */
    @Test
    void servesGeneratedPageFamilies() {
        RestAssured.port = 8082;
        assertTrue(RestAssured.get("/modules/arquillian-core/").then().statusCode(200).extract().asString()
                .contains("Arquillian Core"));
        assertTrue(RestAssured.get("/blog/2011/04/12/arquillian-core-1-0-0-final/").then().statusCode(200).extract()
                .asString().contains("1.0.0.Final"));
        assertTrue(RestAssured.get("/community/aslakknutsen").then().statusCode(200).extract().asString()
                .contains("Aslak Knutsen"));
    }

    /** Confirms the contributors route renders the production card-grid structure. */
    @Test
    void rendersContributorsPage() {
        RestAssured.port = 8082;
        String body = RestAssured.get("/community/contributors/").then().statusCode(200).extract().asString();
        assertTrue(body.contains("class=\"contributors\""));
        assertTrue(body.contains("data-bs-toggle=\"modal\""));
        assertTrue(body.contains("data-bs-dismiss=\"modal\""));
        assertTrue(body.contains("<h3 data-bs-toggle=\"modal\" data-bs-target=\"#alrubinger\" role=\"button\" tabindex=\"0\">Andrew Lee Rubinger</h3>"));
        assertTrue(body.contains("Hantsy Bai"));
        assertTrue(body.contains("Contributors"));
        assertTrue(body.contains(">Blog</a>"));
        assertTrue(body.contains(">Twitter</a>"));
        assertTrue(body.contains(">GitHub</a>"));
    }

    /** Confirms the legacy site scripts receive the jQuery runtime before execution. */
    @Test
    void initializesJQueryForLegacySiteScripts() throws Exception {
        String jqueryBootstrap = Files.readString(Path.of("web/00-jquery.js"));
        assertTrue(jqueryBootstrap.contains("window.jQuery"));
    }

    /** Confirms the homepage contributor spotlight renders the static identity mapping. */
    @Test
    void rendersContributorSpotlight() {
        RestAssured.port = 8082;
        String body = RestAssured.get("/").then().statusCode(200).extract().asString();
        assertTrue(body.contains("Aslak Knutsen"), body);
    }

    /** Confirms the getting-started guide renders the production chapter navigation shell. */
    @Test
    void rendersGettingStartedGuideNavigation() {
        RestAssured.port = 8082;
        String body = RestAssured.get("/guides/getting_started/").then().statusCode(200).extract().asString();
        assertTrue(body.contains("Designed exclusively to teach you how to use Arquillian to write real tests."));
        assertTrue(body.contains("id=\"guides_menu\""));
        assertTrue(body.contains("<body class=\"guide\">"));
        assertTrue(body.contains("id=\"toc\""));
        assertTrue(body.contains("href=\"#_a_closer_look\""));
        assertTrue(body.contains("<h3 id=\"_a_closer_look\">A Closer Look</h3>"));
        assertTrue(body.contains("<div class=\"header\">"));
        assertTrue(body.contains("class=\"admonitionblock note\""));
        assertTrue(body.contains("class=\"greenbar\""));
        assertTrue(body.contains("class=\"admonitionblock important\""));
        assertTrue(body.contains("<h2>Getting Started</h2>"));
        assertTrue(body.contains("<a href=\"/community/mojavelinux\">Dan Allen</a>"));
        assertTrue(body.contains("Translations:</strong>"));
        assertTrue(body.contains("Level:</strong> First Steps"));
        assertTrue(body.contains("Tags:</strong> cdi, weld, maven, forge, eclipse"));
        assertTrue(body.contains("Last Update:</strong> Nov 20, 2017"));
    }

}
