package com.findit.model;

public enum ItemType {
    LOST("Lost Item", "badge-lost", "Lost by Owner"),
    FOUND("Found Item", "badge-found", "Reported Found");

    private final String displayName;
    private final String badgeClass;
    private final String description;

    ItemType(String displayName, String badgeClass, String description) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getDescription() {
        return description;
    }
}
