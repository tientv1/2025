package com.assignment.repository;

import java.util.*;
import com.assignment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    List<Order> findByUserUsername(String username);

    @Query(value = "SELECT FORMAT(o.order_date, 'MM/yyyy') as month, " +
           "CAST(SUM(od.price * od.quantity) as float) as revenue " +
           "FROM orders o " +
           "JOIN order_detail od ON o.id = od.order_id " +
           "WHERE YEAR(o.order_date) = YEAR(GETDATE()) " +
           "GROUP BY FORMAT(o.order_date, 'MM/yyyy')", 
           nativeQuery = true)
    List<Object[]> findMonthlyRevenue();
}
