package com.assignment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private String fullname;

    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false, columnDefinition = "bit default 1")
    private boolean active = true;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}