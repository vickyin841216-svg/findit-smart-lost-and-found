package com.findit.model;

public enum ItemCategory {
    ELECTRONICS("Electronics & Gadgets", "fa-laptop", "#3b82f6"),
    WALLETS_BAGS("Wallets, Bags & Backpacks", "fa-wallet", "#f59e0b"),
    KEYS("Keys & Keychains", "fa-key", "#10b981"),
    CARDS_DOCUMENTS("IDs, Cards & Documents", "fa-id-card", "#8b5cf6"),
    CLOTHING_ACCESSORIES("Clothing & Accessories", "fa-shirt", "#ec4899"),
    BOOKS_STATIONERY("Books & Study Material", "fa-book", "#6366f1"),
    JEWELRY_WATCHES("Jewelry, Watches & Eyewear", "fa-gem", "#eab308"),
    OTHERS("Miscellaneous Items", "fa-box-open", "#64748b");

    private final String displayName;
    private final String iconClass;
    private final String badgeColor;

    ItemCategory(String displayName, String iconClass, String badgeColor) {
        this.displayName = displayName;
        this.iconClass = iconClass;
        this.badgeColor = badgeColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconClass() {
        return iconClass;
    }

    public String getBadgeColor() {
        return badgeColor;
    }
}
