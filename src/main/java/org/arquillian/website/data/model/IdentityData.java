package org.arquillian.website.data.model;

import io.quarkiverse.roq.data.runtime.annotations.DataMapping;
import java.util.Comparator;
import java.util.List;

/** Binds the manually maintained identity array independently from generated page data. */
@DataMapping(value = "identities", type = DataMapping.Type.ARRAY_FILE, required = true)
public record IdentityData(List<Identity> identities) {

    /** Creates an immutable identity collection sorted by the final name part for stable rendering. */
    public IdentityData {
        identities = identities.stream().sorted(Comparator.comparing(IdentityData::lastName)
                .thenComparing(Identity::name)).toList();
    }

    /** Returns the final whitespace-separated name part, supporting single- and multi-part names. */
    private static String lastName(Identity identity) {
        String name = identity.name().trim();
        return name.substring(name.lastIndexOf(' ') + 1);
    }

    /** Describes one manually maintained contributor identity and its public profile links. */
    public record Identity(String githubId, String name, String role, String avatarUrl, String url,
            String company, String location, String bio, String blogUrl, String twitterUrl, boolean core,
            boolean contributor, boolean speaker, boolean translator, List<String> repositories) {
        /** Creates an immutable identity record while accepting omitted repository lists. */
        public Identity {
            repositories = repositories == null ? List.of() : List.copyOf(repositories);
        }
    }
}
