package com.findit.controller;

import com.findit.model.*;
import com.findit.service.ClaimService;
import com.findit.service.ItemService;
import com.findit.service.matching.SmartMatchingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ItemApiController {

    private final ItemService itemService;
    private final ClaimService claimService;
    private final SmartMatchingService smartMatchingService;

    public ItemApiController(ItemService itemService, ClaimService claimService, SmartMatchingService smartMatchingService) {
        this.itemService = itemService;
        this.claimService = claimService;
        this.smartMatchingService = smartMatchingService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getItems(
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(itemService.searchItems(type, category, status, keyword));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item created = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemService.getItemById(id).isPresent()) {
            itemService.deleteItem(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/items/{id}/matches")
    public ResponseEntity<List<MatchResult>> getSmartMatches(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(item -> {
                    List<MatchResult> matches = item.isLost() ?
                            smartMatchingService.findMatchesForLostItem(item) :
                            smartMatchingService.findMatchesForFoundItem(item);
                    return ResponseEntity.ok(matches);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(itemService.getSystemStatistics());
    }

    @PostMapping("/claims")
    public ResponseEntity<Claim> createClaim(@Valid @RequestBody Claim claim) {
        Claim created = claimService.submitClaim(claim);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
