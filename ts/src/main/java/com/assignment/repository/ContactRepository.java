package com.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
}
