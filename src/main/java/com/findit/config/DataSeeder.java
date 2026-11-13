package com.findit.config;

import com.findit.model.*;
import com.findit.repository.ClaimRepository;
import com.findit.repository.ItemRepository;
import com.findit.repository.NotificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;
    private final NotificationRepository notificationRepository;

    public DataSeeder(ItemRepository itemRepository, ClaimRepository claimRepository, NotificationRepository notificationRepository) {
        this.itemRepository = itemRepository;
        this.claimRepository = claimRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) {
        if (itemRepository.count() > 0) {
            return; // Already seeded
        }

        // 1. Lost Item: MacBook Pro 14"
        Item lostMacBook = new Item(
                "Apple MacBook Pro 14-inch Space Gray",
                ItemType.LOST,
                ItemCategory.ELECTRONICS,
                "Apple",
                "Space Gray",
                "Space Gray M2 Pro MacBook with a small scratch on bottom left corner and a GitHub Octocat sticker.",
                "Science Block, 3rd Floor Library Room 302",
                LocalDate.now().minusDays(2),
                "Alice Johnson",
                "alice.j@university.edu",
                "+1-555-0192"
        );
        lostMacBook.setModelNumber("A2779");
        lostMacBook.setSerialNumberOrSecretMark("C02G876XMD6T / GitHub sticker");
        lostMacBook.setStatus(ItemStatus.MATCHED);
        lostMacBook.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=600&q=80");

        // 2. Found Item matching MacBook
        Item foundMacBook = new Item(
                "MacBook Pro Laptop in Space Gray with Sticker",
                ItemType.FOUND,
                ItemCategory.ELECTRONICS,
                "Apple",
                "Space Gray",
                "Found a space gray Apple MacBook Pro left behind on a study desk. Has a black tech decal sticker.",
                "Science Block, 3rd Floor Quiet Study Area",
                LocalDate.now().minusDays(1),
                "Campus Security Desk",
                "security@university.edu",
                "+1-555-0100"
        );
        foundMacBook.setStorageLocation("Main Security Office Locker #104");
        foundMacBook.setSerialNumberOrSecretMark("C02G876XMD6T");
        foundMacBook.setImageUrl("https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?auto=format&fit=crop&w=600&q=80");

        // 3. Lost Item: Brown Fossil Wallet
        Item lostWallet = new Item(
                "Brown Leather Fossil Trifold Wallet",
                ItemType.LOST,
                ItemCategory.WALLETS_BAGS,
                "Fossil",
                "Brown",
                "Dark brown leather wallet containing university ID card, metro card, and a family photo inside.",
                "Student Union Cafeteria, near food counter",
                LocalDate.now().minusDays(3),
                "David Lee",
                "david.lee@university.edu",
                "+1-555-0184"
        );
        lostWallet.setSerialNumberOrSecretMark("Student ID #STU-99214 inside");
        lostWallet.setStatus(ItemStatus.UNDER_REVIEW);
        lostWallet.setImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=600&q=80");

        // 4. Found Item matching Wallet
        Item foundWallet = new Item(
                "Fossil Brown Leather Wallet with Student Cards",
                ItemType.FOUND,
                ItemCategory.WALLETS_BAGS,
                "Fossil",
                "Brown",
                "Brown men's leather wallet turned in by dining staff. Contains ID card and transport pass.",
                "Student Union Cafeteria Table #14",
                LocalDate.now().minusDays(2),
                "Lost & Found Central Desk",
                "lostfound@university.edu",
                "+1-555-0101"
        );
        foundWallet.setStorageLocation("Desk Drawer B, Safe Box #2");
        foundWallet.setStatus(ItemStatus.UNDER_REVIEW);
        foundWallet.setImageUrl("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=600&q=80");

        // 5. Lost Item: Sony Headphones
        Item lostHeadphones = new Item(
                "Sony WH-1000XM5 Noise Cancelling Headphones",
                ItemType.LOST,
                ItemCategory.ELECTRONICS,
                "Sony",
                "Black",
                "Matte black Sony over-ear headphones inside a gray zippered protective hard case.",
                "Central Campus Gymnasium, Locker Room Bench",
                LocalDate.now().minusDays(4),
                "Sarah Connor",
                "sarah.c@university.edu",
                "+1-555-0144"
        );
        lostHeadphones.setStatus(ItemStatus.OPEN);
        lostHeadphones.setImageUrl("https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=600&q=80");

        // 6. Found Item matching Headphones
        Item foundHeadphones = new Item(
                "Black Sony Wireless Over-Ear Headphones",
                ItemType.FOUND,
                ItemCategory.ELECTRONICS,
                "Sony",
                "Black",
                "Black Sony premium headphones found in carrying case left on a gym bench.",
                "Gymnasium Fitness Center",
                LocalDate.now().minusDays(3),
                "Coach Miller",
                "gymstaff@university.edu",
                "+1-555-0160"
        );
        foundHeadphones.setStorageLocation("Gym Reception Counter");
        foundHeadphones.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80");

        // 7. Lost Item: Car Keys with Lanyard
        Item lostKeys = new Item(
                "Toyota Car Key Fob with Red Lanyard & Gym Tag",
                ItemType.LOST,
                ItemCategory.KEYS,
                "Toyota",
                "Black & Silver",
                "Smart key fob with a red Toyota neck lanyard, a mini LED flashlight, and a yellow membership barcode tag.",
                "East Campus Parking Lot B Walkway",
                LocalDate.now().minusDays(1),
                "Michael Chang",
                "mchang@university.edu",
                "+1-555-0177"
        );
        lostKeys.setStatus(ItemStatus.OPEN);
        lostKeys.setImageUrl("https://images.unsplash.com/photo-1616788494707-ec28f08d05a1?auto=format&fit=crop&w=600&q=80");

        // 8. Found Item: Calculas Textbook
        Item foundBook = new Item(
                "Calculus: Early Transcendentals (9th Edition) Textbook",
                ItemType.FOUND,
                ItemCategory.BOOKS_STATIONERY,
                "Cengage",
                "Blue & White",
                "Hardcover calculus textbook found in Room 102. Has handwritten notes on chapter 4 bookmark.",
                "Mathematics Hall Room 102",
                LocalDate.now().minusDays(5),
                "Prof. Roberts",
                "mathdesk@university.edu",
                "+1-555-0122"
        );
        foundBook.setStorageLocation("Math Department Office Shelf #3");
        foundBook.setImageUrl("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80");

        itemRepository.saveAll(List.of(
                lostMacBook, foundMacBook,
                lostWallet, foundWallet,
                lostHeadphones, foundHeadphones,
                lostKeys, foundBook
        ));

        // Seed a sample Claim for David Lee's wallet
        Claim sampleClaim = new Claim(
                foundWallet,
                "David Lee",
                "david.lee@university.edu",
                "+1-555-0184",
                "The wallet has my student ID card #STU-99214 and a metro card expiring in Dec. Inside the zipper slot is an old photo.",
                "STU-99214"
        );
        sampleClaim.setStatus(ClaimStatus.PENDING);
        sampleClaim.setAdminNotes("Awaiting claimant photo ID verification at reception.");
        claimRepository.save(sampleClaim);

        // Seed notifications
        Notification notif1 = new Notification(
                "alice.j@university.edu",
                "🔥 Smart Match (88% Confidence) Detected!",
                "FindIt AI detected a found 'MacBook Pro Laptop in Space Gray with Sticker' at Science Block Study Area that closely matches your lost item.",
                foundMacBook.getId(),
                "MATCH_ALERT"
        );
        Notification notif2 = new Notification(
                "admin@findit.local",
                "New Claim Submitted for Review",
                "David Lee has submitted proof of ownership for 'Fossil Brown Leather Wallet'. Please review the claim.",
                foundWallet.getId(),
                "CLAIM_SUBMITTED"
        );
        Notification notif3 = new Notification(
                "david.lee@university.edu",
                "Claim Status Update: Under Review",
                "Your ownership verification claim for Fossil Brown Leather Wallet is currently being reviewed by desk staff.",
                foundWallet.getId(),
                "CLAIM_STATUS"
        );

        notificationRepository.saveAll(List.of(notif1, notif2, notif3));
    }
}
