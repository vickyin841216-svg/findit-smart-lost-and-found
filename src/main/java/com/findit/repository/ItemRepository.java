package com.findit.repository;

import com.findit.model.Item;
import com.findit.model.ItemCategory;
import com.findit.model.ItemStatus;
import com.findit.model.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByTypeOrderByCreatedAtDesc(ItemType type);

    List<Item> findByTypeAndStatusNotOrderByCreatedAtDesc(ItemType type, ItemStatus status);

    List<Item> findByTypeAndStatus(ItemType type, ItemStatus status);

    List<Item> findTop6ByOrderByCreatedAtDesc();

    long countByType(ItemType type);

    long countByTypeAndStatus(ItemType type, ItemStatus status);

    long countByStatus(ItemStatus status);

    @Query("SELECT i FROM Item i WHERE " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.color) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY i.createdAt DESC")
    List<Item> searchItems(
            @Param("type") ItemType type,
            @Param("category") ItemCategory category,
            @Param("status") ItemStatus status,
            @Param("keyword") String keyword
    );

    @Query("SELECT i.category, COUNT(i) FROM Item i GROUP BY i.category")
    List<Object[]> countItemsByCategory();
}
