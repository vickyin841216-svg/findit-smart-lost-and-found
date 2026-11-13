package com.findit.model;

public enum ClaimStatus {
    PENDING("Pending Verification", "badge-warning", "Awaiting admin / desk review"),
    APPROVED("Approved - Ready for Pickup", "badge-success", "Verified! Claimant given pickup authorization code"),
    REJECTED("Rejected", "badge-danger", "Claim proof did not match item details"),
    COMPLETED("Completed / Handed Over", "badge-primary", "Item physically verified and picked up");

    private final String label;
    private final String cssClass;
    private final String description;

    ClaimStatus(String label, String cssClass, String description) {
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
