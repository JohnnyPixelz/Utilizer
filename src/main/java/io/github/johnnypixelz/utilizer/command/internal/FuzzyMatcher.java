package io.github.johnnypixelz.utilizer.command.internal;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Utility for fuzzy string matching using Levenshtein distance.
 * Used to provide "Did you mean X?" suggestions for mistyped commands.
 */
public final class FuzzyMatcher {

    private static final int DEFAULT_MAX_DISTANCE = 3;

    private FuzzyMatcher() {
    }

    /**
     * Finds the closest matching string from a collection of candidates.
     *
     * @param candidates   the possible matches
     * @param input        the input string to match
     * @param maxDistance  maximum edit distance to consider (default 3)
     * @return the closest match, or empty if no match within distance
     */
    public static Optional<String> findClosest(Collection<String> candidates, String input, int maxDistance) {
        if (candidates == null || candidates.isEmpty() || input == null || input.isEmpty()) {
            return Optional.empty();
        }

        String lowerInput = input.toLowerCase();
        String closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            int distance = levenshteinDistance(lowerInput, candidate.toLowerCase());
            if (distance < minDistance && distance <= maxDistance) {
                minDistance = distance;
                closest = candidate;
            }
        }

        return Optional.ofNullable(closest);
    }

    /**
     * Finds the closest matching string using default max distance.
     *
     * @param candidates the possible matches
     * @param input      the input string to match
     * @return the closest match, or empty if no match within distance
     */
    public static Optional<String> findClosest(Collection<String> candidates, String input) {
        return findClosest(candidates, input, DEFAULT_MAX_DISTANCE);
    }

    /**
     * Finds all strings within the max distance, sorted by distance.
     *
     * @param candidates   the possible matches
     * @param input        the input string to match
     * @param maxDistance  maximum edit distance to consider
     * @return list of matches sorted by distance (closest first)
     */
    public static List<String> findSimilar(Collection<String> candidates, String input, int maxDistance) {
        if (candidates == null || candidates.isEmpty() || input == null || input.isEmpty()) {
            return List.of();
        }

        String lowerInput = input.toLowerCase();

        return candidates.stream()
                .map(c -> new Match(c, levenshteinDistance(lowerInput, c.toLowerCase())))
                .filter(m -> m.distance <= maxDistance)
                .sorted(Comparator.comparingInt(m -> m.distance))
                .map(m -> m.value)
                .toList();
    }

    /**
     * Finds all strings within default max distance, sorted by distance.
     *
     * @param candidates the possible matches
     * @param input      the input string to match
     * @return list of matches sorted by distance (closest first)
     */
    public static List<String> findSimilar(Collection<String> candidates, String input) {
        return findSimilar(candidates, input, DEFAULT_MAX_DISTANCE);
    }

    /**
     * Calculates the Levenshtein (edit) distance between two strings.
     * The distance is the minimum number of single-character edits
     * (insertions, deletions, or substitutions) needed to change one string into the other.
     *
     * @param s1 the first string
     * @param s2 the second string
     * @return the edit distance
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int len1 = s1.length();
        int len2 = s2.length();

        // Optimization: if one string is empty, distance is length of other
        if (len1 == 0) return len2;
        if (len2 == 0) return len1;

        // Use two arrays instead of full matrix for memory efficiency
        int[] prev = new int[len2 + 1];
        int[] curr = new int[len2 + 1];

        // Initialize first row
        for (int j = 0; j <= len2; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            curr[0] = i;

            for (int j = 1; j <= len2; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(
                        Math.min(curr[j - 1] + 1, prev[j] + 1),
                        prev[j - 1] + cost
                );
            }

            // Swap arrays
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[len2];
    }

    /**
     * Suggests a message like "Did you mean 'X'?" if there's a close match.
     *
     * @param candidates the possible matches
     * @param input      the input string
     * @return the suggestion message, or empty if no close match
     */
    public static Optional<String> suggestMessage(Collection<String> candidates, String input) {
        return findClosest(candidates, input)
                .map(match -> "Did you mean '" + match + "'?");
    }

    /**
     * Suggests a message with multiple options.
     *
     * @param candidates  the possible matches
     * @param input       the input string
     * @param maxSuggestions maximum number of suggestions to include
     * @return the suggestion message, or empty if no close matches
     */
    public static Optional<String> suggestMessage(Collection<String> candidates, String input, int maxSuggestions) {
        List<String> similar = findSimilar(candidates, input);
        if (similar.isEmpty()) {
            return Optional.empty();
        }

        if (similar.size() == 1) {
            return Optional.of("Did you mean '" + similar.get(0) + "'?");
        }

        List<String> limited = similar.stream().limit(maxSuggestions).toList();
        return Optional.of("Did you mean one of: " + String.join(", ", limited) + "?");
    }

    private record Match(String value, int distance) {
    }

}
