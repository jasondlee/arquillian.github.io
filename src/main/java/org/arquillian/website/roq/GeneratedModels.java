package org.arquillian.website.roq;

import java.util.List;

/** Defines the typed page-facing records shared by generated Roq routes and Qute. */
public final class GeneratedModels {

    private GeneratedModels() {
    }

    /** Groups all generated page families loaded for one site build. */
    public record SiteData(List<ModulePage> modules, List<ReleasePage> releases, List<NewsItem> news) {
        /** Creates generated page data without optional homepage news records. */
        public SiteData(List<ModulePage> modules, List<ReleasePage> releases) {
            this(modules, releases, List.of());
        }

        /** Creates an immutable generated-data aggregate. */
        public SiteData {
            modules = List.copyOf(modules);
            releases = List.copyOf(releases);
            news = news == null ? List.of() : List.copyOf(news);
        }

        /** Returns an empty generated-data aggregate. */
        public static SiteData empty() {
            return new SiteData(List.of(), List.of(), List.of());
        }
    }

    /** Describes a generated module page. */
    public record ModulePage(String slug, String title, String description, String owner, String url,
            List<String> artifacts, List<String> dependencies) {
        /** Creates an immutable module page model. */
        public ModulePage {
            artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
            dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
        }
    }

    /** Describes a generated release page and its calendar entry. */
    public record ReleasePage(String slug, String title, String version, String component, String date, String url,
            boolean latest, List<ReleaseNote> releaseNotes) {
        /** Creates an immutable release page model. */
        public ReleasePage {
            releaseNotes = releaseNotes == null ? List.of() : List.copyOf(releaseNotes);
        }
    }

    /** Describes one ordered news entry, including optional release details for full blog rendering. */
    public record NewsItem(String title, String url, String author, String authorUrl, String avatarUrl,
            String published, String summary, String intro, String descriptionHeading, String description,
            String component, String componentUrl, String version, String versionUrl, String releaseDate,
            String artifactGroup, String artifactPath, String releaseNoteCategory,
            List<NewsDependency> compiledAgainst, List<String> artifacts, List<ReleaseNote> releaseNotes,
            List<String> tags,
            String previousUrl, String previousTitle, int postNumber, int postTotal) {
        /** Creates an immutable news entry while allowing older curated records to remain summary-only. */
        public NewsItem {
            compiledAgainst = compiledAgainst == null ? List.of() : List.copyOf(compiledAgainst);
            artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
            releaseNotes = releaseNotes == null ? List.of() : List.copyOf(releaseNotes);
            tags = tags == null ? List.of() : List.copyOf(tags);
        }
    }

    /** Describes a release-note issue rendered on a release page. */
    public record ReleaseNote(int number, String title, String category, String url) {
    }

    /** Describes one named dependency and its production module link. */
    public record NewsDependency(String name, String version, String url) {
    }

}
