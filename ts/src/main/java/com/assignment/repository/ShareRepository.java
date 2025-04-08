package com.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.entity.Share;

@Repository
public interface ShareRepository extends JpaRepository<Share, Integer> {
}
