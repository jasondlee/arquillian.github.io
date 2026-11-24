package org.arquillian.website.data.model;

import io.quarkiverse.roq.data.runtime.annotations.DataMapping;
import java.util.List;

/** Binds the ordered team usernames used by the invasion team page. */
@DataMapping(value = "team", type = DataMapping.Type.ARRAY_FILE, required = true)
public record TeamData(List<String> team) {

    /** Creates an immutable team list so page rendering cannot mutate source data. */
    public TeamData {
        team = List.copyOf(team);
    }
}
