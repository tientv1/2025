package com.assignment.entity;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private Date contactDate;
}
