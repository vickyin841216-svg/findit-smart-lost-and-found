package com.findit.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResult implements Comparable<MatchResult> {

    private Item lostItem;
    private Item foundItem;
    private double overallScore; // 0.0 to 1.0
    private double categoryScore;
    private double textScore;
    private double locationScore;
    private double dateScore;
    private double attributeScore;
    private String confidenceLabel;
    private String confidenceBadgeClass;
    private List<String> matchReasons = new ArrayList<>();

    public MatchResult() {
    }

    public MatchResult(Item lostItem, Item foundItem, double overallScore) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.overallScore = overallScore;
        computeConfidence();
    }

    public void computeConfidence() {
        if (overallScore >= 0.75) {
            this.confidenceLabel = "HIGH MATCH";
            this.confidenceBadgeClass = "badge-high-match";
        } else if (overallScore >= 0.50) {
            this.confidenceLabel = "POTENTIAL MATCH";
            this.confidenceBadgeClass = "badge-potential-match";
        } else {
            this.confidenceLabel = "LOW PROBABILITY";
            this.confidenceBadgeClass = "badge-low-match";
        }
    }

    public int getPercentage() {
        return (int) Math.round(overallScore * 100);
    }

    public int getCategoryPercentage() {
        return (int) Math.round(categoryScore * 100);
    }

    public int getTextPercentage() {
        return (int) Math.round(textScore * 100);
    }

    public int getLocationPercentage() {
        return (int) Math.round(locationScore * 100);
    }

    public int getDatePercentage() {
        return (int) Math.round(dateScore * 100);
    }

    public int getAttributePercentage() {
        return (int) Math.round(attributeScore * 100);
    }

    @Override
    public int compareTo(MatchResult other) {
        // Higher score first
        return Double.compare(other.overallScore, this.overallScore);
    }

    // Getters and Setters

    public Item getLostItem() {
        return lostItem;
    }

    public void setLostItem(Item lostItem) {
        this.lostItem = lostItem;
    }

    public Item getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(Item foundItem) {
        this.foundItem = foundItem;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
        computeConfidence();
    }

    public double getCategoryScore() {
        return categoryScore;
    }

    public void setCategoryScore(double categoryScore) {
        this.categoryScore = categoryScore;
    }

    public double getTextScore() {
        return textScore;
    }

    public void setTextScore(double textScore) {
        this.textScore = textScore;
    }

    public double getLocationScore() {
        return locationScore;
    }

    public void setLocationScore(double locationScore) {
        this.locationScore = locationScore;
    }

    public double getDateScore() {
        return dateScore;
    }

    public void setDateScore(double dateScore) {
        this.dateScore = dateScore;
    }

    public double getAttributeScore() {
        return attributeScore;
    }

    public void setAttributeScore(double attributeScore) {
        this.attributeScore = attributeScore;
    }

    public String getConfidenceLabel() {
        return confidenceLabel;
    }

    public String getConfidenceBadgeClass() {
        return confidenceBadgeClass;
    }

    public List<String> getMatchReasons() {
        return matchReasons;
    }

    public void setMatchReasons(List<String> matchReasons) {
        this.matchReasons = matchReasons;
    }

    public void addReason(String reason) {
        this.matchReasons.add(reason);
    }
}
