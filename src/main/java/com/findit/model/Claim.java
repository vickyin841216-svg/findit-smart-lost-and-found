package com.findit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotBlank(message = "Claimant name is required")
    @Column(nullable = false, length = 100)
    private String claimantName;

    @NotBlank(message = "Email address is required")
    @Column(nullable = false, length = 120)
    private String claimantEmail;

    @Column(length = 30)
    private String claimantPhone;

    @NotBlank(message = "Proof of ownership description is required")
    @Column(nullable = false, length = 2000)
    private String proofDescription;

    @Column(length = 255)
    private String serialNumberProvided;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(length = 1000)
    private String adminNotes;

    /**
     * 6-digit verification code generated when claim is approved.
     * The owner must show this code at the Lost & Found desk to collect the item.
     */
    @Column(length = 20)
    private String verificationCode;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    public Claim() {
    }

    public Claim(Item item, String claimantName, String claimantEmail, String claimantPhone,
                 String proofDescription, String serialNumberProvided) {
        this.item = item;
        this.claimantName = claimantName;
        this.claimantEmail = claimantEmail;
        this.claimantPhone = claimantPhone;
        this.proofDescription = proofDescription;
        this.serialNumberProvided = serialNumberProvided;
        this.status = ClaimStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ClaimStatus.PENDING;
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getClaimantEmail() {
        return claimantEmail;
    }

    public void setClaimantEmail(String claimantEmail) {
        this.claimantEmail = claimantEmail;
    }

    public String getClaimantPhone() {
        return claimantPhone;
    }

    public void setClaimantPhone(String claimantPhone) {
        this.claimantPhone = claimantPhone;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }

    public String getSerialNumberProvided() {
        return serialNumberProvided;
    }

    public void setSerialNumberProvided(String serialNumberProvided) {
        this.serialNumberProvided = serialNumberProvided;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
