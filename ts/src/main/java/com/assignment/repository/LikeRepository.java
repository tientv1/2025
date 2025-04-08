package com.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
}
