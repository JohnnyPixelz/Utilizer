package io.github.johnnypixelz.utilizer.maven;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Dependency {

    public static Dependency of(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String repoUrl) {
        return new Dependency(groupId, artifactId, version, repoUrl);
    }

    public static Dependency of(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        return new Dependency(groupId, artifactId, version);
    }

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String repoUrl;

    public Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        this(groupId, artifactId, version, "https://repo1.maven.org/maven2");
    }

    public Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String repoUrl) {
        this.groupId = Objects.requireNonNull(groupId, "groupId");
        this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
        this.version = Objects.requireNonNull(version, "version");
        this.repoUrl = Objects.requireNonNull(repoUrl, "repoUrl");
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getVersion() {
        return this.version;
    }

    public String getRepoUrl() {
        return this.repoUrl;
    }

    public URL getUrl() throws MalformedURLException {
        String repo = this.repoUrl;
        if (!repo.endsWith("/")) {
            repo += "/";
        }
        repo += "%s/%s/%s/%s-%s.jar";

        String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
        return new URL(url);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Dependency)) return false;
        final Dependency other = (Dependency) o;
        return this.getGroupId().equals(other.getGroupId()) &&
                this.getArtifactId().equals(other.getArtifactId()) &&
                this.getVersion().equals(other.getVersion()) &&
                this.getRepoUrl().equals(other.getRepoUrl());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getGroupId().hashCode();
        result = result * PRIME + this.getArtifactId().hashCode();
        result = result * PRIME + this.getVersion().hashCode();
        result = result * PRIME + this.getRepoUrl().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LibraryLoader.Dependency(" +
                "groupId=" + this.getGroupId() + ", " +
                "artifactId=" + this.getArtifactId() + ", " +
                "version=" + this.getVersion() + ", " +
                "repoUrl=" + this.getRepoUrl() + ")";
    }

}
