package com.krishna.MobileBackendProjectPhase1.repository;

import com.krishna.MobileBackendProjectPhase1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByMobileNumberAndIdNot(String mobileNumber, Long id);

    Optional<User> findByEmail(String mail);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.status = 'ACTIVE'
            """)
    List<User> findAllActiveUsers();

    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN u.orders o
            """)
    List<User> findUsersWithOrders();

    @Query("""
            SELECT u
            FROM User u
            JOIN u.orders o
            GROUP BY u
            HAVING COUNT(o) > 5
            """)
    List<User> findUsersHavingMoreThanFiveOrders();

    @Query("""
            SELECT u
            FROM User u
            WHERE u.enabled = true
            AND u.updatedAt < :cutoffDate
            """)
    List<User> findInactiveUsers(
            @Param("cutoffDate") LocalDateTime cutoffDate
    );
}