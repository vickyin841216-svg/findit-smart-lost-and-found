package com.findit.service.matching;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateProximityMatcher {

    /**
     * Evaluates temporal plausibility:
     * Ideally, lostDate <= foundDate.
     * Higher score for items found shortly after they were lost.
     */
    public double calculateDateSimilarity(LocalDate lostDate, LocalDate foundDate) {
        if (lostDate == null || foundDate == null) {
            return 0.5; // Neutral
        }

        long daysBetween = ChronoUnit.DAYS.between(lostDate, foundDate);

        // Found exactly on the same day or day after
        if (daysBetween >= 0 && daysBetween <= 1) {
            return 1.0;
        }

        // Found within 2 to 4 days
        if (daysBetween > 1 && daysBetween <= 4) {
            return 0.90;
        }

        // Found within 5 to 10 days
        if (daysBetween > 4 && daysBetween <= 10) {
            return 0.75;
        }

        // Found within 11 to 30 days
        if (daysBetween > 10 && daysBetween <= 30) {
            return 0.55;
        }

        // Found after 30+ days
        if (daysBetween > 30) {
            return Math.max(0.20, 0.50 - (daysBetween / 100.0));
        }

        // If foundDate is before lostDate (e.g. human mistake in remembering exact date)
        if (daysBetween == -1 || daysBetween == -2) {
            return 0.70; // 1-2 day human error tolerance
        }

        // Found way before supposed loss date is improbable
        return 0.10;
    }
}
