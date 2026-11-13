package com.findit;

import com.findit.service.matching.TextSimilarityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TextSimilarityUtilTest {

    @Test
    @DisplayName("Should detect identical texts with 100% score")
    void testIdenticalStrings() {
        double score = TextSimilarityUtil.calculateSimilarity("MacBook Pro 14", "MacBook Pro 14");
        assertEquals(1.0, score, 0.001);
    }

    @Test
    @DisplayName("Should detect high similarity in slight variations and substrings")
    void testSubstringAndVariations() {
        double score = TextSimilarityUtil.calculateSimilarity("Apple MacBook Pro 14 inch", "MacBook Pro 14");
        assertTrue(score >= 0.70, "Substring should yield score >= 0.70, got: " + score);
    }

    @Test
    @DisplayName("Should extract keywords without stop words")
    void testKeywordExtraction() {
        Set<String> keywords = TextSimilarityUtil.extractKeywords("The lost black leather Fossil wallet with cards");
        assertFalse(keywords.contains("the"));
        assertFalse(keywords.contains("lost"));
        assertFalse(keywords.contains("with"));
        assertTrue(keywords.contains("black"));
        assertTrue(keywords.contains("leather"));
        assertTrue(keywords.contains("fossil"));
        assertTrue(keywords.contains("wallet"));
    }

    @Test
    @DisplayName("Should calculate Jaccard similarity accurately")
    void testJaccardSimilarity() {
        Set<String> set1 = Set.of("apple", "macbook", "gray");
        Set<String> set2 = Set.of("apple", "macbook", "silver");
        // Intersection = 2, Union = 4 -> 2/4 = 0.5
        double jaccard = TextSimilarityUtil.calculateJaccard(set1, set2);
        assertEquals(0.5, jaccard, 0.001);
    }
}
