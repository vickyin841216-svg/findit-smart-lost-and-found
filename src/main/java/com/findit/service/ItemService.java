package com.findit.service;

import com.findit.model.Item;
import com.findit.model.ItemCategory;
import com.findit.model.ItemStatus;
import com.findit.model.ItemType;
import com.findit.repository.ItemRepository;
import com.findit.service.matching.SmartMatchingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final SmartMatchingService smartMatchingService;

    public ItemService(ItemRepository itemRepository, SmartMatchingService smartMatchingService) {
        this.itemRepository = itemRepository;
        this.smartMatchingService = smartMatchingService;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getItemsByType(ItemType type) {
        return itemRepository.findByTypeOrderByCreatedAtDesc(type);
    }

    public List<Item> getRecentItems() {
        return itemRepository.findTop6ByOrderByCreatedAtDesc();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Transactional
    public Item saveItem(Item item) {
        Item saved = itemRepository.save(item);
        // Automatically run Smart Match analysis in the background
        try {
            smartMatchingService.checkAndNotifySmartMatches(saved);
        } catch (Exception ignored) {
            // Non-blocking notification
        }
        return saved;
    }

    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> searchItems(ItemType type, ItemCategory category, ItemStatus status, String keyword) {
        return itemRepository.searchItems(type, category, status, keyword);
    }

    public Map<String, Object> getSystemStatistics() {
        long totalLost = itemRepository.countByType(ItemType.LOST);
        long totalFound = itemRepository.countByType(ItemType.FOUND);
        long claimedCount = itemRepository.countByStatus(ItemStatus.CLAIMED);
        long matchedCount = itemRepository.countByStatus(ItemStatus.MATCHED);
        long openLost = itemRepository.countByTypeAndStatus(ItemType.LOST, ItemStatus.OPEN);
        long openFound = itemRepository.countByTypeAndStatus(ItemType.FOUND, ItemStatus.OPEN);

        long totalItems = totalLost + totalFound;
        double recoveryRate = totalItems > 0 ? ((double) claimedCount / totalItems) * 100.0 : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLost", totalLost);
        stats.put("totalFound", totalFound);
        stats.put("totalItems", totalItems);
        stats.put("claimedCount", claimedCount);
        stats.put("matchedCount", matchedCount);
        stats.put("openLost", openLost);
        stats.put("openFound", openFound);
        stats.put("recoveryRate", Math.round(recoveryRate * 10.0) / 10.0);

        List<Object[]> categoryCounts = itemRepository.countItemsByCategory();
        Map<String, Long> categoryMap = new LinkedHashMap<>();
        for (Object[] row : categoryCounts) {
            ItemCategory cat = (ItemCategory) row[0];
            Long count = (Long) row[1];
            categoryMap.put(cat.getDisplayName(), count);
        }
        stats.put("categoryCounts", categoryMap);

        return stats;
    }
}
