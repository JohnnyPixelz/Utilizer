package io.github.johnnypixelz.utilizer.papi;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and matches placeholder template patterns.
 *
 * <p>Converts template patterns like {@code "leaderboard_{skill}_top_{position}"}
 * into regex patterns that can match and extract values:
 *
 * <ul>
 *   <li>{@code "stat_{type}"} matches "stat_mining" and captures type="mining"</li>
 *   <li>{@code "leaderboard_{skill}_top_{position}"} matches "leaderboard_mining_top_1"
 *       and captures skill="mining", position="1"</li>
 * </ul>
 */
class PlaceholderPattern {

    private static final Pattern TEMPLATE_VAR_PATTERN = Pattern.compile("\\{([^}]+)}");

    private final String template;
    private final Pattern regex;
    private final List<String> parameterNames;

    /**
     * Create a new placeholder pattern from a template string.
     *
     * @param template the template pattern (e.g., "stat_{type}")
     */
    PlaceholderPattern(@NotNull String template) {
        this.template = template;
        this.parameterNames = new ArrayList<>();

        // Parse template and build regex
        Matcher matcher = TEMPLATE_VAR_PATTERN.matcher(template);
        StringBuilder regexBuilder = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // Add literal text before this variable (escaped for regex)
            String literal = template.substring(lastEnd, matcher.start());
            regexBuilder.append(Pattern.quote(literal));

            // Add capture group for the variable
            String paramName = matcher.group(1);
            parameterNames.add(paramName);

            // Use non-greedy match for all but last parameter
            regexBuilder.append("(.+?)");

            lastEnd = matcher.end();
        }

        // Add any remaining literal text
        if (lastEnd < template.length()) {
            regexBuilder.append(Pattern.quote(template.substring(lastEnd)));
        }

        // If we have parameters, make the last one greedy
        if (!parameterNames.isEmpty()) {
            String regexStr = regexBuilder.toString();
            // Replace the last (.+?) with (.+)
            int lastNonGreedy = regexStr.lastIndexOf("(.+?)");
            if (lastNonGreedy >= 0) {
                regexStr = regexStr.substring(0, lastNonGreedy) + "(.+)" + regexStr.substring(lastNonGreedy + 5);
            }
            this.regex = Pattern.compile("^" + regexStr + "$");
        } else {
            // No parameters - exact match
            this.regex = Pattern.compile("^" + Pattern.quote(template) + "$");
        }
    }

    /**
     * Get the original template string.
     *
     * @return the template
     */
    @NotNull
    String getTemplate() {
        return template;
    }

    /**
     * Check if this pattern has any template parameters.
     *
     * @return true if the template contains {xxx} variables
     */
    boolean hasParameters() {
        return !parameterNames.isEmpty();
    }

    /**
     * Get the parameter names in order.
     *
     * @return list of parameter names
     */
    @NotNull
    List<String> getParameterNames() {
        return Collections.unmodifiableList(parameterNames);
    }

    /**
     * Try to match an input string against this pattern.
     *
     * @param input the input string to match
     * @param raw   the original raw params string
     * @return PlaceholderArgs if matched, empty if no match
     */
    @NotNull
    Optional<PlaceholderArgs> match(@NotNull String input, @NotNull String raw) {
        Matcher matcher = regex.matcher(input);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        // Extract captured groups
        Map<String, String> args = new LinkedHashMap<>();
        for (int i = 0; i < parameterNames.size(); i++) {
            args.put(parameterNames.get(i), matcher.group(i + 1));
        }

        return Optional.of(new PlaceholderArgsImpl(args, raw));
    }

    /**
     * Try to match an input string against this pattern.
     *
     * @param input the input string to match (also used as raw)
     * @return PlaceholderArgs if matched, empty if no match
     */
    @NotNull
    Optional<PlaceholderArgs> match(@NotNull String input) {
        return match(input, input);
    }

}
