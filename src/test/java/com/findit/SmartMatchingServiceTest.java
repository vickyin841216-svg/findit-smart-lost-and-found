package com.findit;

import com.findit.model.*;
import com.findit.repository.ItemRepository;
import com.findit.repository.NotificationRepository;
import com.findit.service.matching.SmartMatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SmartMatchingServiceTest {

    private SmartMatchingService matchingService;
    private ItemRepository itemRepository;
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        notificationRepository = Mockito.mock(NotificationRepository.class);
        matchingService = new SmartMatchingService(itemRepository, notificationRepository);
    }

    @Test
    @DisplayName("Should detect high match for matching lost and found items")
    void testHighConfidenceMatch() {
        Item lost = new Item(
                "Apple MacBook Pro 14 M2",
                ItemType.LOST,
                ItemCategory.ELECTRONICS,
                "Apple",
                "Space Gray",
                "Space Gray laptop with github sticker lost at science library",
                "Science Block Library 3rd Floor",
                LocalDate.now().minusDays(2),
                "Alice",
                "alice@test.com",
                "123"
        );

        Item found = new Item(
                "MacBook Pro Space Gray Laptop",
                ItemType.FOUND,
                ItemCategory.ELECTRONICS,
                "Apple",
                "Space Gray",
                "Found an Apple laptop with tech sticker left on quiet study desk",
                "Science Block 3rd Floor",
                LocalDate.now().minusDays(1),
                "Security",
                "sec@test.com",
                "456"
        );

        MatchResult result = matchingService.calculateMatch(lost, found);

        assertNotNull(result);
        assertTrue(result.getOverallScore() >= 0.70, "Expected score >= 0.70, got: " + result.getOverallScore());
        assertTrue(result.getConfidenceLabel().equals("HIGH MATCH") || result.getConfidenceLabel().equals("POTENTIAL MATCH"));
        assertFalse(result.getMatchReasons().isEmpty());
    }

    @Test
    @DisplayName("Should penalize items in different categories")
    void testCategoryMismatch() {
        Item lost = new Item(
                "MacBook Pro Laptop",
                ItemType.LOST,
                ItemCategory.ELECTRONICS,
                "Apple",
                "Space Gray",
                "Laptop",
                "Library",
                LocalDate.now(),
                "Alice",
                "alice@test.com",
                "123"
        );

        Item found = new Item(
                "Leather Wallet",
                ItemType.FOUND,
                ItemCategory.WALLETS_BAGS,
                "Fossil",
                "Brown",
                "Wallet",
                "Cafeteria",
                LocalDate.now(),
                "Staff",
                "staff@test.com",
                "456"
        );

        MatchResult result = matchingService.calculateMatch(lost, found);

        assertTrue(result.getOverallScore() < 0.40, "Unrelated items should yield low score, got: " + result.getOverallScore());
    }
}
