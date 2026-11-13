package com.findit.service.matching;

import java.util.Locale;
import java.util.Set;

public class LocationMatcher {

    public double calculateLocationSimilarity(String loc1, String loc2) {
        if (loc1 == null || loc2 == null) {
            return 0.3; // Neutral baseline if location unknown
        }

        String l1 = loc1.trim().toLowerCase(Locale.ROOT);
        String l2 = loc2.trim().toLowerCase(Locale.ROOT);

        if (l1.equals(l2)) {
            return 1.0;
        }

        // Substring / contained place (e.g. "Library 3rd floor" in "Main Campus Central Library 3rd Floor")
        if (l1.contains(l2) || l2.contains(l1)) {
            return 0.90;
        }

        Set<String> tokens1 = TextSimilarityUtil.extractKeywords(l1);
        Set<String> tokens2 = TextSimilarityUtil.extractKeywords(l2);

        double jaccard = TextSimilarityUtil.calculateJaccard(tokens1, tokens2);
        if (jaccard > 0.4) {
            return Math.min(1.0, 0.5 + jaccard);
        }

        // Check common building / zone keywords
        String[] zones = {"library", "cafeteria", "canteen", "lab", "gym", "hostel", "block", "hall", "auditorium", "parking"};
        for (String zone : zones) {
            if (l1.contains(zone) && l2.contains(zone)) {
                return 0.70; // Same major building / facility!
            }
        }

        return Math.max(0.1, jaccard);
    }
}
