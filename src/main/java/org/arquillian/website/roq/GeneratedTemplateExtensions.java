package org.arquillian.website.roq;

import io.quarkus.qute.TemplateExtension;
import java.io.IOException;
import java.util.List;
import org.arquillian.website.roq.GeneratedModels.ModulePage;
import org.arquillian.website.roq.GeneratedModels.NewsItem;

/** Exposes staged provider records to ordinary Roq content templates. */
@TemplateExtension(namespace = "generated")
public final class GeneratedTemplateExtensions {

    private GeneratedTemplateExtensions() {
    }

    /** Returns generated module records for the documentation index. */
    public static List<ModulePage> modules() {
        try {
            return new GeneratedDataExtension().load().modules();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load generated module data", exception);
        }
    }

    /** Returns homepage news in the provider-defined production order. */
    public static List<NewsItem> news() {
        try {
            return new GeneratedDataExtension().load().news();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load generated homepage news", exception);
        }
    }
}
