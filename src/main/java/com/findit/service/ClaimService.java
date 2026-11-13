package com.findit.service;

import com.findit.model.*;
import com.findit.repository.ClaimRepository;
import com.findit.repository.ItemRepository;
import com.findit.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final NotificationRepository notificationRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository, NotificationRepository notificationRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Claim> getClaimsByItemId(Long itemId) {
        return claimRepository.findByItemIdOrderByCreatedAtDesc(itemId);
    }

    public Optional<Claim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }

    @Transactional
    public Claim submitClaim(Claim claim) {
        Item item = claim.getItem();
        if (item != null) {
            item.setStatus(ItemStatus.UNDER_REVIEW);
            itemRepository.save(item);
        }

        Claim saved = claimRepository.save(claim);

        // Notify desk/admin of new claim
        Notification notification = new Notification(
                "admin@findit.local",
                "New Claim Submitted for Item #" + (item != null ? item.getId() : ""),
                String.format("Claimant %s (%s) submitted a claim for: %s",
                        claim.getClaimantName(), claim.getClaimantEmail(), item != null ? item.getTitle() : "Item"),
                item != null ? item.getId() : null,
                "CLAIM_SUBMITTED"
        );
        notificationRepository.save(notification);

        return saved;
    }

    @Transactional
    public Claim approveClaim(Long claimId, String adminNotes) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with id: " + claimId));

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setAdminNotes(adminNotes);
        claim.setReviewedAt(LocalDateTime.now());

        // Generate 6-digit handover verification code
        int code = 100000 + secureRandom.nextInt(900000);
        String verificationCode = String.valueOf(code);
        claim.setVerificationCode(verificationCode);

        // Alert the claimant
        Item item = claim.getItem();
        String message = String.format(
                "Congratulations! Your claim for '%s' has been APPROVED. Your pickup verification PIN is [%s]. Present this code and your photo ID at the Lost & Found desk (%s) to collect your item.",
                item.getTitle(), verificationCode, item.getStorageLocation() != null ? item.getStorageLocation() : "Security Office"
        );

        Notification notification = new Notification(
                claim.getClaimantEmail(),
                "Claim Approved - Pickup Code: " + verificationCode,
                message,
                item.getId(),
                "CLAIM_APPROVED"
        );
        notificationRepository.save(notification);

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim rejectClaim(Long claimId, String adminNotes) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with id: " + claimId));

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setAdminNotes(adminNotes);
        claim.setReviewedAt(LocalDateTime.now());

        // Revert item status to OPEN
        Item item = claim.getItem();
        item.setStatus(ItemStatus.OPEN);
        itemRepository.save(item);

        Notification notification = new Notification(
                claim.getClaimantEmail(),
                "Claim Update for " + item.getTitle(),
                "Your claim for '" + item.getTitle() + "' could not be verified. Note: " + (adminNotes != null ? adminNotes : "Proof did not match item records."),
                item.getId(),
                "CLAIM_REJECTED"
        );
        notificationRepository.save(notification);

        return claimRepository.save(claim);
    }

    @Transactional
    public boolean verifyAndCompleteHandover(Long claimId, String enteredCode) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with id: " + claimId));

        if (claim.getVerificationCode() != null && claim.getVerificationCode().equals(enteredCode.trim())) {
            claim.setStatus(ClaimStatus.COMPLETED);
            Item item = claim.getItem();
            item.setStatus(ItemStatus.CLAIMED);
            itemRepository.save(item);
            claimRepository.save(claim);
            return true;
        }
        return false;
    }

    public long getPendingClaimsCount() {
        return claimRepository.countByStatus(ClaimStatus.PENDING);
    }
}
