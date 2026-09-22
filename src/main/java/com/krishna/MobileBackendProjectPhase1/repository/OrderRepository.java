package com.krishna.MobileBackendProjectPhase1.repository;

import com.krishna.MobileBackendProjectPhase1.dto.projection.BestSellingProductProjection;
import com.krishna.MobileBackendProjectPhase1.dto.projection.OrderAnalyticsProjection;
import com.krishna.MobileBackendProjectPhase1.dto.projection.TopCustomerProjection;
import com.krishna.MobileBackendProjectPhase1.entity.Order;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "driver"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByDriver(User driver, Pageable pageable);

    Page<Order> findByDriverId(Long driverId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT o
            FROM Order o
            WHERE o.id = :id
            """)
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT o
            FROM Order o
            WHERE o.totalAmount >= :amount
            """)
    Page<Order> findHighValueOrders(@Param("amount") BigDecimal amount, Pageable pageable);

    @Query("""
            SELECT DISTINCT o.user
            FROM Order o
            """)
    List<User> findUsersWithOrders();

    @Query("""
            SELECT o.user
            FROM Order o
            GROUP BY o.user
            HAVING COUNT(o) > 5
            """)
    List<User> findUsersWithMoreThanFiveOrders();

    @Query("""
            SELECT u.id AS userId,
                   CONCAT(u.firstName, ' ', u.lastName) AS name,
                   u.email AS email,
                   COUNT(o.id) AS totalOrders,
                   SUM(o.totalAmount) AS totalSpent
            FROM Order o
            JOIN o.user u
            GROUP BY u.id, u.firstName, u.lastName, u.email
            ORDER BY SUM(o.totalAmount) DESC
            """)
    Page<TopCustomerProjection> findTopCustomers(Pageable pageable);

    @Query(value = """
            SELECT COUNT(*) AS totalOrders,
                   COALESCE(SUM(total_amount), 0) AS totalRevenue,
                   COALESCE(AVG(total_amount), 0) AS averageOrderValue
            FROM orders
            """, nativeQuery = true)
    OrderAnalyticsProjection getOrderStatistics();

    @Query(value = """
            SELECT p.id AS productId,
                   p.name AS productName,
                   SUM(oi.quantity) AS totalQuantitySold,
                   SUM(oi.subtotal) AS totalRevenue
            FROM order_items oi
            JOIN products p ON p.id = oi.product_id
            JOIN orders o ON o.id = oi.order_id
            GROUP BY p.id, p.name
            ORDER BY SUM(oi.quantity) DESC
            """, nativeQuery = true)
    Page<BestSellingProductProjection> findBestSellingProducts(Pageable pageable);

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.createdAt >= :startOfDay
            AND o.createdAt < :startOfNextDay
            """)
    long countByCreatedAtBetween(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay
    );

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.createdAt >= :startOfDay
            AND o.createdAt < :startOfNextDay
            """)
    BigDecimal sumTotalAmountByCreatedAtBetween(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay
    );
}