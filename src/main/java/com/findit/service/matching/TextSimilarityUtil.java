package com.findit.service.matching;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * High-performance text similarity utility combining:
 * 1. Normalized Levenshtein edit distance
 * 2. Token-level Jaccard similarity with stop-word filtering
 * 3. Exact substring and acronym bonus
 */
public final class TextSimilarityUtil {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "all", "am", "an", "and", "any", "are",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both",
            "but", "by", "for", "from", "had", "has", "have", "having", "he", "her", "here",
            "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is",
            "it", "its", "itself", "just", "me", "more", "most", "my", "myself", "no", "nor",
            "not", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves",
            "out", "over", "own", "same", "she", "should", "so", "some", "such", "than", "that",
            "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they",
            "this", "those", "through", "to", "too", "under", "until", "up", "very", "was",
            "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "with",
            "lost", "found", "item", "please", "help"
    );

    private static final Pattern WORD_SPLIT = Pattern.compile("[^a-zA-Z0-9]+");

    private TextSimilarityUtil() {
    }

    /**
     * Calculates a composite similarity score between two texts [0.0, 1.0].
     */
    public static double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        String s1 = text1.trim().toLowerCase(Locale.ROOT);
        String s2 = text2.trim().toLowerCase(Locale.ROOT);

        if (s1.isEmpty() && s2.isEmpty()) {
            return 1.0;
        }
        if (s1.isEmpty() || s2.isEmpty()) {
            return 0.0;
        }
        if (s1.equals(s2)) {
            return 1.0;
        }

        // 1. Check substring containment
        if (s1.contains(s2) || s2.contains(s1)) {
            double lengthRatio = (double) Math.min(s1.length(), s2.length()) / Math.max(s1.length(), s2.length());
            return Math.max(0.75, 0.60 + 0.40 * lengthRatio);
        }

        // 2. Token-level Jaccard similarity
        Set<String> tokens1 = extractKeywords(s1);
        Set<String> tokens2 = extractKeywords(s2);
        double jaccardScore = calculateJaccard(tokens1, tokens2);

        // 3. Normalized Levenshtein distance
        double levenshteinScore = calculateNormalizedLevenshtein(s1, s2);

        // Blend token overlap (60%) and character sequence similarity (40%)
        return (jaccardScore * 0.60) + (levenshteinScore * 0.40);
    }

    /**
     * Extracts cleaned alphanumeric keywords omitting stop words.
     */
    public static Set<String> extractKeywords(String input) {
        if (input == null || input.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(WORD_SPLIT.split(input.toLowerCase(Locale.ROOT)))
                .filter(w -> w.length() >= 2 && !STOP_WORDS.contains(w))
                .collect(Collectors.toSet());
    }

    /**
     * Jaccard Index = |Intersection| / |Union|
     */
    public static double calculateJaccard(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() || set2.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    /**
     * Normalized Levenshtein: 1.0 - (editDistance / maxLen)
     */
    public static double calculateNormalizedLevenshtein(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        int distance = levenshteinDistance(s1, s2);
        return Math.max(0.0, 1.0 - ((double) distance / maxLen));
    }

    private static int levenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int j = 0; j <= s2.length(); j++) {
            costs[j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= s2.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        s1.charAt(i - 1) == s2.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[s2.length()];
    }
}
