package org.arquillian.website.roq;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.TemplateExtension;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.arquillian.website.data.model.IdentityData.Identity;

/** Exposes the manually maintained identity snapshot to ordinary Qute content pages. */
@TemplateExtension(namespace = "identities")
public final class IdentityTemplateExtensions {

    private IdentityTemplateExtensions() {
    }

    /** Loads contributors for the homepage spotlight and contributor content page. */
    public static List<Identity> contributors() {
        try {
            return List.of(new ObjectMapper().readValue(Path.of("data/identities.json").toFile(), Identity[].class));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load static identity data", exception);
        }
    }
}
