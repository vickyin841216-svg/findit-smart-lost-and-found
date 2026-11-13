package com.findit.repository;

import com.findit.model.Claim;
import com.findit.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByItemIdOrderByCreatedAtDesc(Long itemId);

    List<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status);

    List<Claim> findAllByOrderByCreatedAtDesc();

    long countByStatus(ClaimStatus status);

    List<Claim> findByClaimantEmailOrderByCreatedAtDesc(String claimantEmail);
}
