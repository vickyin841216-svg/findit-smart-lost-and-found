package com.findit.model;

public enum ItemStatus {
    OPEN("Active", "badge-success", "Item is actively open in the system"),
    UNDER_REVIEW("Under Review", "badge-warning", "A claim is currently being reviewed"),
    MATCHED("Smart Matched", "badge-info", "High confidence match detected"),
    CLAIMED("Returned to Owner", "badge-primary", "Successfully verified and returned"),
    CLOSED("Closed / Archived", "badge-secondary", "Archived or disposed after retention period");

    private final String label;
    private final String cssClass;
    private final String description;

    ItemStatus(String label, String cssClass, String description) {
        this.label = label;
        this.cssClass = cssClass;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getDescription() {
        return description;
    }
}
