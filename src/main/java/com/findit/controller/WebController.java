package com.findit.controller;

import com.findit.model.*;
import com.findit.service.ClaimService;
import com.findit.service.ItemService;
import com.findit.service.NotificationService;
import com.findit.service.matching.SmartMatchingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class WebController {

    private final ItemService itemService;
    private final ClaimService claimService;
    private final NotificationService notificationService;
    private final SmartMatchingService smartMatchingService;

    public WebController(ItemService itemService, ClaimService claimService,
                         NotificationService notificationService, SmartMatchingService smartMatchingService) {
        this.itemService = itemService;
        this.claimService = claimService;
        this.notificationService = notificationService;
        this.smartMatchingService = smartMatchingService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("categories", ItemCategory.values());
        model.addAttribute("unreadNotificationCount", notificationService.getUnreadCount());
        model.addAttribute("pendingClaimsCount", claimService.getPendingClaimsCount());
    }

    /**
     * Dashboard view: statistics, category distributions, recent lost & found items.
     */
    @GetMapping("/")
    public String index(Model model) {
        java.util.Map<String, Object> stats = itemService.getSystemStatistics();
        model.addAttribute("stats", stats);
        model.addAttribute("recentItems", itemService.getRecentItems());
        model.addAttribute("systemMatches", smartMatchingService.findAllSystemMatches());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Long> categoryCounts = (java.util.Map<String, Long>) stats.get("categoryCounts");
        model.addAttribute("categoryLabels", new java.util.ArrayList<>(categoryCounts.keySet()));
        model.addAttribute("categoryValues", new java.util.ArrayList<>(categoryCounts.values()));
        return "index";
    }

    /**
     * Lost & Found Catalog view with search and filter.
     */
    @GetMapping("/items")
    public String listItems(
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Item> items = itemService.searchItems(type, category, status, keyword);
        model.addAttribute("items", items);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "items/list";
    }

    /**
     * Display Report Item Form (Lost or Found).
     */
    @GetMapping("/items/report")
    public String reportForm(@RequestParam(required = false, defaultValue = "LOST") ItemType type, Model model) {
        Item item = new Item();
        item.setType(type);
        item.setDateOccurred(LocalDate.now());
        model.addAttribute("item", item);
        return "items/report";
    }

    /**
     * Submit Report Item Form.
     */
    @PostMapping("/items/report")
    public String submitReport(@Valid @ModelAttribute("item") Item item,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            return "items/report";
        }

        Item saved = itemService.saveItem(item);
        redirectAttributes.addFlashAttribute("successMessage",
                "Your " + saved.getType().getDisplayName() + " has been successfully posted! FindIt is now analyzing matches.");
        return "redirect:/items/" + saved.getId();
    }

    /**
     * Item Details View with instant Smart Match preview and Claim modal.
     */
    @GetMapping("/items/{id}")
    public String itemDetail(@PathVariable Long id, Model model) {
        Item item = itemService.getItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));

        List<MatchResult> smartMatches = item.isLost() ?
                smartMatchingService.findMatchesForLostItem(item) :
                smartMatchingService.findMatchesForFoundItem(item);

        List<Claim> itemClaims = claimService.getClaimsByItemId(id);

        model.addAttribute("item", item);
        model.addAttribute("smartMatches", smartMatches);
        model.addAttribute("itemClaims", itemClaims);
        model.addAttribute("newClaim", new Claim());
        return "items/detail";
    }

    /**
     * Dedicated Smart Matching Engine comparison page.
     */
    @GetMapping("/items/{id}/matches")
    public String viewMatches(@PathVariable Long id, Model model) {
        Item item = itemService.getItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));

        List<MatchResult> matches = item.isLost() ?
                smartMatchingService.findMatchesForLostItem(item) :
                smartMatchingService.findMatchesForFoundItem(item);

        model.addAttribute("item", item);
        model.addAttribute("matches", matches);
        return "items/match";
    }

    /**
     * Submit Claim for a Found Item.
     */
    @PostMapping("/items/{id}/claim")
    public String submitClaim(@PathVariable Long id,
                              @RequestParam String claimantName,
                              @RequestParam String claimantEmail,
                              @RequestParam(required = false) String claimantPhone,
                              @RequestParam String proofDescription,
                              @RequestParam(required = false) String serialNumberProvided,
                              RedirectAttributes redirectAttributes) {

        Item item = itemService.getItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));

        Claim claim = new Claim(item, claimantName, claimantEmail, claimantPhone, proofDescription, serialNumberProvided);
        claimService.submitClaim(claim);

        redirectAttributes.addFlashAttribute("successMessage",
                "Your claim has been submitted to the Lost & Found desk for verification! You will receive notification updates.");
        return "redirect:/items/" + id;
    }

    /**
     * Claims verification desk for administrators / lost & found staff.
     */
    @GetMapping("/claims")
    public String listClaims(Model model) {
        model.addAttribute("claims", claimService.getAllClaims());
        return "claims/list";
    }

    /**
     * Admin approves a claim.
     */
    @PostMapping("/claims/{id}/approve")
    public String approveClaim(@PathVariable Long id,
                               @RequestParam(required = false, defaultValue = "Claim approved after verification.") String adminNotes,
                               RedirectAttributes redirectAttributes) {
        Claim claim = claimService.approveClaim(id, adminNotes);
        redirectAttributes.addFlashAttribute("successMessage",
                "Claim #" + id + " approved! Verification Code [" + claim.getVerificationCode() + "] sent to " + claim.getClaimantEmail());
        return "redirect:/claims";
    }

    /**
     * Admin rejects a claim.
     */
    @PostMapping("/claims/{id}/reject")
    public String rejectClaim(@PathVariable Long id,
                              @RequestParam(required = false, defaultValue = "Proof provided did not match item attributes.") String adminNotes,
                              RedirectAttributes redirectAttributes) {
        claimService.rejectClaim(id, adminNotes);
        redirectAttributes.addFlashAttribute("infoMessage", "Claim #" + id + " has been rejected.");
        return "redirect:/claims";
    }

    /**
     * Verify pickup PIN code and finalize physical handover.
     */
    @PostMapping("/claims/{id}/verify-pickup")
    public String verifyPickup(@PathVariable Long id,
                               @RequestParam String verificationCode,
                               RedirectAttributes redirectAttributes) {
        boolean success = claimService.verifyAndCompleteHandover(id, verificationCode);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Handover Verified! Code matched successfully. Item is marked as RETURNED TO OWNER.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid Verification Code! Handover could not be confirmed.");
        }
        return "redirect:/claims";
    }

    /**
     * Notifications feed view.
     */
    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("notifications", notificationService.getRecentNotifications());
        return "notifications";
    }

    /**
     * Mark notification as read.
     */
    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    /**
     * Mark all notifications as read.
     */
    @PostMapping("/notifications/read-all")
    public String markAllRead() {
        notificationService.markAllAsRead();
        return "redirect:/notifications";
    }
}
