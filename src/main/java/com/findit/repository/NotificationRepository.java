package com.findit.repository;

import com.findit.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);

    List<Notification> findTop15ByOrderByCreatedAtDesc();

    long countByRecipientEmailAndIsReadFalse(String recipientEmail);

    long countByIsReadFalse();
}
