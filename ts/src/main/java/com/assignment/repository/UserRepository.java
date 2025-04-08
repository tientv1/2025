package com.assignment.repository;

import com.assignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    List<User> findByActiveTrue();

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT TOP(:limit) u.fullname, u.email, " +
           "COUNT(o.id) as total_orders, " +
           "COALESCE(SUM(od.price * od.quantity), 0) as total_spent, " +
           "COALESCE(AVG(od.price * od.quantity), 0) as avg_order " +
           "FROM users u " +
           "LEFT JOIN orders o ON u.id = o.user_id " +
           "LEFT JOIN order_detail od ON o.id = od.order_id " +
           "GROUP BY u.fullname, u.email " +
           "ORDER BY total_spent DESC", 
           nativeQuery = true)
    List<Object[]> findTopCustomersByRevenue(@Param("limit") int limit);
}
