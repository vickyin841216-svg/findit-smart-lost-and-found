package com.findit.service.matching;

import com.findit.model.*;
import com.findit.repository.ItemRepository;
import com.findit.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartMatchingService {

    private final ItemRepository itemRepository;
    private final NotificationRepository notificationRepository;
    private final LocationMatcher locationMatcher = new LocationMatcher();
    private final DateProximityMatcher dateMatcher = new DateProximityMatcher();

    @Value("${findit.match.threshold:0.55}")
    private double matchThreshold = 0.55;

    @Value("${findit.match.weight.category:0.25}")
    private double weightCategory = 0.25;

    @Value("${findit.match.weight.text:0.35}")
    private double weightText = 0.35;

    @Value("${findit.match.weight.location:0.20}")
    private double weightLocation = 0.20;

    @Value("${findit.match.weight.date:0.10}")
    private double weightDate = 0.10;

    @Value("${findit.match.weight.attributes:0.10}")
    private double weightAttributes = 0.10;

    public SmartMatchingService(ItemRepository itemRepository, NotificationRepository notificationRepository) {
        this.itemRepository = itemRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Finds smart matching Found items for a specific Lost item.
     */
    public List<MatchResult> findMatchesForLostItem(Item lostItem) {
        if (lostItem == null || !lostItem.isLost()) {
            return Collections.emptyList();
        }

        // Compare against active / open found items
        List<Item> candidateFoundItems = itemRepository.findByTypeAndStatusNotOrderByCreatedAtDesc(
                ItemType.FOUND, ItemStatus.CLAIMED);

        List<MatchResult> results = new ArrayList<>();
        for (Item foundItem : candidateFoundItems) {
            MatchResult match = calculateMatch(lostItem, foundItem);
            if (match.getOverallScore() >= matchThreshold) {
                results.add(match);
            }
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Finds smart matching Lost items for a newly reported Found item.
     */
    public List<MatchResult> findMatchesForFoundItem(Item foundItem) {
        if (foundItem == null || !foundItem.isFound()) {
            return Collections.emptyList();
        }

        List<Item> candidateLostItems = itemRepository.findByTypeAndStatusNotOrderByCreatedAtDesc(
                ItemType.LOST, ItemStatus.CLAIMED);

        List<MatchResult> results = new ArrayList<>();
        for (Item lostItem : candidateLostItems) {
            MatchResult match = calculateMatch(lostItem, foundItem);
            if (match.getOverallScore() >= matchThreshold) {
                results.add(match);
            }
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Finds all system-wide high-confidence matches across open items.
     */
    public List<MatchResult> findAllSystemMatches() {
        List<Item> lostItems = itemRepository.findByTypeAndStatus(ItemType.LOST, ItemStatus.OPEN);
        List<Item> foundItems = itemRepository.findByTypeAndStatus(ItemType.FOUND, ItemStatus.OPEN);

        List<MatchResult> allMatches = new ArrayList<>();
        for (Item lost : lostItems) {
            for (Item found : foundItems) {
                MatchResult result = calculateMatch(lost, found);
                if (result.getOverallScore() >= matchThreshold) {
                    allMatches.add(result);
                }
            }
        }

        Collections.sort(allMatches);
        return allMatches;
    }

    /**
     * Evaluates two items and produces detailed MatchResult.
     */
    public MatchResult calculateMatch(Item lostItem, Item foundItem) {
        MatchResult result = new MatchResult();
        result.setLostItem(lostItem);
        result.setFoundItem(foundItem);

        // 1. Category Match (Weight: 25%)
        double categoryScore = 0.0;
        if (lostItem.getCategory() == foundItem.getCategory()) {
            categoryScore = 1.0;
            result.addReason("Category matches exactly: " + lostItem.getCategory().getDisplayName());
        } else {
            categoryScore = 0.15; // Mismatch penalty
        }
        result.setCategoryScore(categoryScore);

        // 2. Text Similarity on Title & Description (Weight: 35%)
        double titleSim = TextSimilarityUtil.calculateSimilarity(lostItem.getTitle(), foundItem.getTitle());
        double descSim = TextSimilarityUtil.calculateSimilarity(lostItem.getDescription(), foundItem.getDescription());
        double textScore = (titleSim * 0.65) + (descSim * 0.35);
        result.setTextScore(textScore);

        if (titleSim > 0.65) {
            result.addReason(String.format("High title resemblance (%d%%)", (int) (titleSim * 100)));
        }
        if (descSim > 0.50) {
            result.addReason(String.format("Description keywords align (%d%%)", (int) (descSim * 100)));
        }

        // 3. Location Proximity (Weight: 20%)
        double locationScore = locationMatcher.calculateLocationSimilarity(lostItem.getLocation(), foundItem.getLocation());
        result.setLocationScore(locationScore);
        if (locationScore >= 0.70) {
            result.addReason(String.format("Location proximity match in '%s'", foundItem.getLocation()));
        }

        // 4. Temporal Window (Weight: 10%)
        double dateScore = dateMatcher.calculateDateSimilarity(lostItem.getDateOccurred(), foundItem.getDateOccurred());
        result.setDateScore(dateScore);
        if (dateScore >= 0.85) {
            result.addReason("Dates correlate well (Found near or shortly after reported lost date)");
        }

        // 5. Attributes: Brand, Color, Serial/Marks (Weight: 10%)
        double attrScore = calculateAttributeSimilarity(lostItem, foundItem, result);
        result.setAttributeScore(attrScore);

        // Calculate Weighted Overall Score
        double overall = (categoryScore * weightCategory)
                + (textScore * weightText)
                + (locationScore * weightLocation)
                + (dateScore * weightDate)
                + (attrScore * weightAttributes);

        // Bonus: If category matches AND title similarity > 0.80, apply synergy boost
        if (categoryScore == 1.0 && titleSim > 0.80) {
            overall = Math.min(1.0, overall + 0.08);
        }

        result.setOverallScore(overall);
        return result;
    }

    private double calculateAttributeSimilarity(Item lost, Item found, MatchResult result) {
        double score = 0.5; // neutral base
        int checks = 0;

        // Brand match
        if (lost.getBrand() != null && !lost.getBrand().isBlank() &&
            found.getBrand() != null && !found.getBrand().isBlank()) {
            checks++;
            if (lost.getBrand().equalsIgnoreCase(found.getBrand())) {
                score += 0.3;
                result.addReason("Identical brand match: " + lost.getBrand());
            } else if (lost.getBrand().toLowerCase().contains(found.getBrand().toLowerCase()) ||
                       found.getBrand().toLowerCase().contains(lost.getBrand().toLowerCase())) {
                score += 0.2;
                result.addReason("Partial brand match: " + lost.getBrand() + " / " + found.getBrand());
            } else {
                score -= 0.15;
            }
        }

        // Color match
        if (lost.getColor() != null && !lost.getColor().isBlank() &&
            found.getColor() != null && !found.getColor().isBlank()) {
            checks++;
            if (lost.getColor().equalsIgnoreCase(found.getColor())) {
                score += 0.2;
                result.addReason("Matching color: " + lost.getColor());
            } else {
                score -= 0.1;
            }
        }

        // Serial / Identifier check
        if (lost.getSerialNumberOrSecretMark() != null && !lost.getSerialNumberOrSecretMark().isBlank() &&
            found.getSerialNumberOrSecretMark() != null && !found.getSerialNumberOrSecretMark().isBlank()) {
            String s1 = lost.getSerialNumberOrSecretMark().trim().toLowerCase();
            String s2 = found.getSerialNumberOrSecretMark().trim().toLowerCase();
            if (s1.equals(s2)) {
                score = 1.0;
                result.addReason("CRITICAL: Exact serial number / unique mark match!");
            }
        }

        return Math.min(1.0, Math.max(0.0, score));
    }

    /**
     * Checks if a newly reported item triggers high-confidence alerts for existing items.
     */
    public void checkAndNotifySmartMatches(Item newItem) {
        List<MatchResult> matches = newItem.isLost() ? 
                findMatchesForLostItem(newItem) : findMatchesForFoundItem(newItem);

        for (MatchResult match : matches) {
            if (match.getOverallScore() >= 0.75) {
                // High match found!
                Item lost = match.getLostItem();
                Item found = match.getFoundItem();

                // Alert the lost item owner
                String message = String.format(
                        "Smart Match Detected (%d%% confidence)! A found item matching '%s' was recorded at %s. Please review and submit your claim.",
                        match.getPercentage(), lost.getTitle(), found.getLocation()
                );

                Notification notification = new Notification(
                        lost.getContactEmail(),
                        "Potential Match for your lost " + lost.getTitle(),
                        message,
                        found.getId(),
                        "MATCH_ALERT"
                );
                notificationRepository.save(notification);

                // Update item status to MATCHED if still OPEN
                if (lost.getStatus() == ItemStatus.OPEN) {
                    lost.setStatus(ItemStatus.MATCHED);
                    itemRepository.save(lost);
                }
            }
        }
    }
}
