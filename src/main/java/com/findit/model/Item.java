package com.findit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotNull(message = "Item type (Lost or Found) is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemType type;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemCategory category;

    @Column(length = 80)
    private String brand;

    @Column(length = 80)
    private String modelNumber;

    @Column(length = 50)
    private String color;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 2000)
    private String description;

    @NotBlank(message = "Location is required")
    @Column(nullable = false, length = 250)
    private String location;

    @Column(length = 150)
    private String storageLocation; // For FOUND items (e.g. "Lost & Found Desk, Bin B4")

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate dateOccurred;

    @NotBlank(message = "Contact name is required")
    @Column(nullable = false, length = 100)
    private String contactName;

    @NotBlank(message = "Contact email is required")
    @Column(nullable = false, length = 120)
    private String contactEmail;

    @Column(length = 30)
    private String contactPhone;

    /**
     * Unique identifier, serial number, or secret mark.
     * For Lost items: provides validation target.
     * For Found items: hidden or sanitized to prevent fraudulent claims!
     */
    @Column(length = 255)
    private String serialNumberOrSecretMark;

    @Column(length = 500)
    private String imageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemStatus status = ItemStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Item() {
    }

    public Item(String title, ItemType type, ItemCategory category, String brand, String color,
                String description, String location, LocalDate dateOccurred,
                String contactName, String contactEmail, String contactPhone) {
        this.title = title;
        this.type = type;
        this.category = category;
        this.brand = brand;
        this.color = color;
        this.description = description;
        this.location = location;
        this.dateOccurred = dateOccurred;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = ItemStatus.OPEN;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ItemStatus.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public LocalDate getDateOccurred() {
        return dateOccurred;
    }

    public void setDateOccurred(LocalDate dateOccurred) {
        this.dateOccurred = dateOccurred;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getSerialNumberOrSecretMark() {
        return serialNumberOrSecretMark;
    }

    public void setSerialNumberOrSecretMark(String serialNumberOrSecretMark) {
        this.serialNumberOrSecretMark = serialNumberOrSecretMark;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isLost() {
        return ItemType.LOST.equals(this.type);
    }

    public boolean isFound() {
        return ItemType.FOUND.equals(this.type);
    }
}
