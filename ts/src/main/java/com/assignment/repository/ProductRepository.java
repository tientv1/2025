package com.assignment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.assignment.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:keyword)")
    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query(value = "SELECT * FROM products WHERE active = 1 ORDER BY NEWID()", nativeQuery = true)
    List<Product> findRandomProducts(int limit);

    @Query(value = "SELECT * FROM products p " +
           "WHERE p.category_id = :categoryId " +
           "ORDER BY " +
           "CASE WHEN :sort = 'name_asc' THEN p.name END ASC, " +
           "CASE WHEN :sort = 'name_desc' THEN p.name END DESC, " +
           "CASE WHEN :sort = 'price_asc' THEN p.price END ASC, " +
           "CASE WHEN :sort = 'price_desc' THEN p.price END DESC", 
           nativeQuery = true)
    List<Product> findByCategoryIdAndSort(@org.springframework.data.repository.query.Param("categoryId") Integer categoryId, @org.springframework.data.repository.query.Param("sort") String sort);
}
